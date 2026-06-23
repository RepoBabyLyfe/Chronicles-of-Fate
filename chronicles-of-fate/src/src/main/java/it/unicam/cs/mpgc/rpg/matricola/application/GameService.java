package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.application.events.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Orchestratore dell'Application Layer. Gestisce il flusso principale
 * nascondendo la complessità del Dominio alla futura Interfaccia Grafica.
 */
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());

    private static final Set<String> BASE_CARDS = Set.of(
            "Risonanza Eterea", "Fenditura Quantica",
            "Sovraccarico Biologico", "Innesto Simbiotico", "Assimilazione Oscura"
    );

    private final GameStateRepository repository;
    private final EventPublisher eventPublisher;
    private CombatManager combatManager;

    private List<Card> customDeckRecipe;
    private PlayerProfile playerProfile;
    private CombatResult lastCombatResult;

    public GameService(GameStateRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;

        this.playerProfile = loadProfileFromSave();
    }

    /**
     * Adapter che traduce le notifiche del dominio in eventi dell'application layer.
     * Questo permette al CombatManager di restare nel domain senza dipendere dagli eventi.
     */
    private CombatEventListener createEventAdapter() {
        return new CombatEventListener() {
            @Override
            public void onEnemyCardPlayed(String cardName, String logMessage, String imagePath) {
                eventPublisher.publish(new EnemyCardPlayedEvent(cardName, logMessage, imagePath));
            }

            @Override
            public void onLogMessage(String message) {
                eventPublisher.publish(new LogEvent(message));
            }

            @Override
            public void onDamageTaken(Character target, int damageAmount, boolean isCritical) {
                eventPublisher.publish(new DamageTakenEvent(target, damageAmount, isCritical));
            }
        };
    }

    /**
     * Tenta di caricare il profilo giocatore dal file di salvataggio.
     * Se non esiste o è corrotto, restituisce un profilo base con le 5 carte starter.
     */
    private PlayerProfile loadProfileFromSave() {
        try {
            if (repository.saveExists()) {
                GameState state = repository.load();
                LOGGER.info("Profilo caricato dal salvataggio: Frammenti("
                        + state.etherFragments() + "), Carte(" + state.unlockedCards().size() + ")");
                return new PlayerProfile(state.etherFragments(), state.unlockedCards(), state.defeatedBosses());
            }
        } catch (Exception e) {
            LOGGER.warning("Errore nel caricamento del profilo: " + e.getMessage()
                    + " — Utilizzo profilo base.");
        }
        return new PlayerProfile(0, BASE_CARDS);
    }

    /**
     * Inizializza una nuova partita da zero.
     */
    public void startNewGame(Character player, Character enemy) {
        EnemyArchetype archetype = GameFactory.getEnemyArchetype(playerProfile.getDefeatedBosses());
        this.combatManager = new CombatManager(player, enemy, createEventAdapter(), archetype);
        this.combatManager.startCombat();
        LOGGER.info("Nuova partita avviata!");

        eventPublisher.publish(new HpChangedEvent(player, player.getCurrentHp(), player.getMaxHp()));
        eventPublisher.publish(new HpChangedEvent(enemy, enemy.getCurrentHp(), enemy.getMaxHp()));
    }

    /**
     * Salva il profilo del giocatore (frammenti + carte sbloccate).
     * Sicuro da chiamare in qualsiasi momento, anche fuori dal combattimento.
     */
    public void saveProfile() {
        GameState state = GameState.profileOnly(
                playerProfile.getEtherFragments(),
                playerProfile.getUnlockedCards(),
                playerProfile.getDefeatedBosses()
        );
        persistState(state);
    }

    /**
     * Salva lo stato completo: profilo + stato del combattimento in corso.
     * Da chiamare SOLO se c'è un combattimento attivo.
     */
    public void saveGame() {
        if (combatManager == null) {
            saveProfile();
            return;
        }

        GameState state = GameState.withCombat(
                combatManager.getPlayer().getCurrentHp(),
                combatManager.getPlayer().getCurrentFocus(),
                combatManager.getEnemy().getCurrentHp(),
                playerProfile.getEtherFragments(),
                playerProfile.getUnlockedCards(),
                playerProfile.getDefeatedBosses()
        );
        persistState(state);
    }

    private void persistState(GameState state) {
        try {
            repository.save(state);
            LOGGER.info("Salvataggio completato: Frammenti("
                    + playerProfile.getEtherFragments() + "), Carte("
                    + playerProfile.getUnlockedCards().size() + ")");
        } catch (Exception e) {
            LOGGER.warning("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Carica una partita precedente e ripristina il profilo giocatore.
     * Ripristina SOLO se il salvataggio contiene dati di combattimento.
     */
    public void loadGame(Character playerBase) {
        try {
            GameState state = repository.load();

            this.playerProfile = new PlayerProfile(state.etherFragments(), state.unlockedCards(), state.defeatedBosses());

            if (state.hasCombatData()) {
                int playerDamageToApply = playerBase.getMaxHp() - state.playerHp();
                if (playerDamageToApply > 0) playerBase.takeDamage(playerDamageToApply);

                playerBase.consumeFocus(playerBase.getCurrentFocus());
                playerBase.restoreFocus(state.playerFocus());

                EnemyArchetype archetype = GameFactory.getEnemyArchetype(state.defeatedBosses());
                Character enemyBase = GameFactory.createEnemy(state.defeatedBosses());
                
                int enemyDamageToApply = enemyBase.getMaxHp() - state.enemyHp();
                if (enemyDamageToApply > 0) enemyBase.takeDamage(enemyDamageToApply);

                this.combatManager = new CombatManager(playerBase, enemyBase, createEventAdapter(), archetype);
                this.combatManager.startCombat();

                eventPublisher.publish(new HpChangedEvent(playerBase, playerBase.getCurrentHp(), playerBase.getMaxHp()));
                eventPublisher.publish(new HpChangedEvent(enemyBase, enemyBase.getCurrentHp(), enemyBase.getMaxHp()));
            } else {
                // Se non ci sono dati di combattimento, usciamo senza inizializzare CombatManager
            }

        } catch (Exception e) {
            LOGGER.warning("Errore durante il caricamento: " + e.getMessage());
        }
    }

    /**
     * Finalizza il combattimento: assegna ricompense e salva il profilo.
     * @param result Il risultato del combattimento concluso.
     */
    public void endCombat(CombatResult result) {
        this.lastCombatResult = result;
        if (result.fragmentsEarned() > 0) {
            playerProfile.addFragments(result.fragmentsEarned());
        }
        if (result.isVictory()) {
            EnemyArchetype archetype = GameFactory.getEnemyArchetype(playerProfile.getDefeatedBosses());
            for (EnemyCard card : archetype.getCardPool()) {
                playerProfile.unlockCard(card.name());
            }
            playerProfile.incrementDefeatedBosses();
        }
        this.combatManager = null;
        saveProfile();
    }

    public CombatResult getLastCombatResult() {
        return lastCombatResult;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public void setCustomDeckRecipe(List<Card> recipe) {
        this.customDeckRecipe = recipe;
    }

    public List<Card> getCustomDeckRecipe() {
        if (customDeckRecipe == null) {
            CardCatalog catalog = new CardCatalog();
            List<Card> defaultDeck = new ArrayList<>();
            for (String cardName : playerProfile.getUnlockedCards()) {
                if (defaultDeck.size() == 5) break;
                catalog.getAllCards().stream()
                        .filter(c -> c.getName().equals(cardName))
                        .findFirst()
                        .ifPresent(defaultDeck::add);
            }
            customDeckRecipe = defaultDeck;
        }
        return new ArrayList<>(customDeckRecipe);
    }

    public Deck getCustomDeck() {
        return new Deck(getCustomDeckRecipe());
    }

    public PlayerProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public void setPlayerProfile(PlayerProfile profile) {
        this.playerProfile = profile;
    }

    public void wipeProfile() {
        this.playerProfile = new PlayerProfile(0, BASE_CARDS);
        this.customDeckRecipe = null;
        this.combatManager = null;
        saveProfile();
    }
}