package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.HpChangedEvent;
import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.domain.CombatResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Orchestratore dell'Application Layer. Gestisce il flusso principale
 * nascondendo la complessità del Dominio alla futura Interfaccia Grafica.
 */
public class GameService {

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

        // Carichiamo il profilo dal salvataggio se esiste, altrimenti profilo base
        this.playerProfile = loadProfileFromSave();
    }

    /**
     * Tenta di caricare il profilo giocatore dal file di salvataggio.
     * Se non esiste o è corrotto, restituisce un profilo base con le 5 carte starter.
     */
    private PlayerProfile loadProfileFromSave() {
        try {
            if (repository.saveExists()) {
                GameState state = repository.load();
                System.out.println("Profilo caricato dal salvataggio: Frammenti("
                        + state.etherFragments() + "), Carte(" + state.unlockedCards().size() + ")");
                return new PlayerProfile(state.etherFragments(), state.unlockedCards());
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento del profilo: " + e.getMessage()
                    + " — Utilizzo profilo base.");
        }
        return new PlayerProfile(0, BASE_CARDS);
    }

    /**
     * Inizializza una nuova partita da zero.
     */
    public void startNewGame(Character player, Character enemy) {
        this.combatManager = new CombatManager(player, enemy, this.eventPublisher);
        this.combatManager.startCombat();
        System.out.println("Nuova partita avviata!");

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
                playerProfile.getUnlockedCards()
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
                playerProfile.getUnlockedCards()
        );
        persistState(state);
    }

    private void persistState(GameState state) {
        try {
            repository.save(state);
            System.out.println("Salvataggio completato: Frammenti("
                    + playerProfile.getEtherFragments() + "), Carte("
                    + playerProfile.getUnlockedCards().size() + ")");
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Carica una partita precedente e ripristina il profilo giocatore.
     * Ripristina SOLO se il salvataggio contiene dati di combattimento.
     */
    public void loadGame(Character playerBase, Character enemyBase) {
        try {
            GameState state = repository.load();

            // 1. Ripristiniamo sempre il Profilo del Giocatore
            this.playerProfile = new PlayerProfile(state.etherFragments(), state.unlockedCards());

            // 2. Ripristiniamo il Combattimento solo se i dati sono validi
            if (state.hasCombatData()) {
                int playerDamageToApply = playerBase.getMaxHp() - state.playerHp();
                if (playerDamageToApply > 0) playerBase.takeDamage(playerDamageToApply);

                playerBase.consumeFocus(playerBase.getCurrentFocus());
                playerBase.restoreFocus(state.playerFocus());

                int enemyDamageToApply = enemyBase.getMaxHp() - state.enemyHp();
                if (enemyDamageToApply > 0) enemyBase.takeDamage(enemyDamageToApply);
            }

            // 3. Riavvio Motore
            this.combatManager = new CombatManager(playerBase, enemyBase, this.eventPublisher);
            this.combatManager.startCombat();

            eventPublisher.publish(new HpChangedEvent(playerBase, playerBase.getCurrentHp(), playerBase.getMaxHp()));
            eventPublisher.publish(new HpChangedEvent(enemyBase, enemyBase.getCurrentHp(), enemyBase.getMaxHp()));

        } catch (Exception e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
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

    public Deck getCustomDeck() {
        if (customDeckRecipe == null) return null;
        return new Deck(new ArrayList<>(customDeckRecipe));
    }

    public PlayerProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public void setPlayerProfile(PlayerProfile profile) {
        this.playerProfile = profile;
    }
}