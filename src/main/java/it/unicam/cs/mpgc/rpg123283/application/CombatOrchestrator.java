package it.unicam.cs.mpgc.rpg123283.application;

import it.unicam.cs.mpgc.rpg123283.application.events.*;
import it.unicam.cs.mpgc.rpg123283.domain.*;
import it.unicam.cs.mpgc.rpg123283.domain.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *gestisce il ciclo di vita del combattimento: avvio, caricamento, fine, e le azioni del giocatore durante lo scontro.
 */
public class CombatOrchestrator {

    private static final Logger LOGGER = Logger.getLogger(CombatOrchestrator.class.getName());

    private final EventPublisher eventPublisher;
    private final CardCatalog cardCatalog;
    private CombatManager combatManager;
    private List<Card> customDeckRecipe;

    public CombatOrchestrator(EventPublisher eventPublisher, CardCatalog cardCatalog) {
        this.eventPublisher = eventPublisher;
        this.cardCatalog = cardCatalog;
    }

    /**
     * inizializza una nuova partita da zero
     */
    public void startNewGame(Character player, Character enemy, PlayerProfile profile) {
        EnemyArchetype archetype = GameFactory.getEnemyArchetype(profile.getDefeatedBosses());
        IEnemyAI ai = new BossAI(archetype);
        this.combatManager = new CombatManager(player, enemy, createEventAdapter(), ai);
        this.combatManager.startCombat();
        LOGGER.info("Nuova partita avviata!");

        eventPublisher.publish(new HpChangedEvent(player, player.getCurrentHp(), player.getMaxHp()));
        eventPublisher.publish(new HpChangedEvent(enemy, enemy.getCurrentHp(), enemy.getMaxHp()));
    }

    /**
     * ripristina un combattimento da uno stato salvato.
     */
    public void loadCombat(Character playerBase, GameState state, PlayerProfile profile) {
        if (!state.hasCombatData())
            return;

        int playerDamageToApply = playerBase.getMaxHp() - state.playerHp();
        if (playerDamageToApply > 0)
            playerBase.takeDamage(playerDamageToApply);

        playerBase.consumeFocus(playerBase.getCurrentFocus());
        playerBase.restoreFocus(state.playerFocus());

        EnemyArchetype archetype = GameFactory.getEnemyArchetype(state.defeatedBosses());
        Character enemyBase = GameFactory.createEnemy(state.defeatedBosses());

        int enemyDamageToApply = enemyBase.getMaxHp() - state.enemyHp();
        if (enemyDamageToApply > 0)
            enemyBase.takeDamage(enemyDamageToApply);

        IEnemyAI ai = new BossAI(archetype);
        this.combatManager = new CombatManager(playerBase, enemyBase, createEventAdapter(), ai);
        this.combatManager.startCombat();

        eventPublisher.publish(new HpChangedEvent(playerBase, playerBase.getCurrentHp(), playerBase.getMaxHp()));
        eventPublisher.publish(new HpChangedEvent(enemyBase, enemyBase.getCurrentHp(), enemyBase.getMaxHp()));
    }

    /**
     * finalizza il combattimento: assegna ricompense e aggiorna il profilo
     * @param result il risultato del combattimento concluso
     * @param profile il profilo del giocatore
     * @return il risultato del combattimento per eventuali utilizzi successivi
     */
    public CombatResult endCombat(CombatResult result, PlayerProfile profile) {
        if (result.fragmentsEarned() > 0) {
            profile.addFragments(result.fragmentsEarned());
        }
        if (result.isVictory()) {
            EnemyArchetype archetype = GameFactory.getEnemyArchetype(profile.getDefeatedBosses());
            for (EnemyCard card : archetype.getCardPool()) {
                profile.unlockCard(card.name());
            }
            profile.incrementDefeatedBosses();
        }
        this.combatManager = null;
        return result;
    }

    /**
     * facade: permette di giocare una carta, nasconde il combatmanager
     */
    public boolean playCard(Card card, Targetable target) {
        if (combatManager == null) {
            LOGGER.warning("Nessun combattimento attivo.");
            return false;
        }
        return combatManager.playCard(card, target);
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public void clearCombat() {
        this.combatManager = null;
    }

    public void setCustomDeckRecipe(List<Card> recipe) {
        this.customDeckRecipe = recipe;
    }

    public List<Card> getCustomDeckRecipe(PlayerProfile profile) {
        if (customDeckRecipe == null) {
            List<Card> defaultDeck = new ArrayList<>();
            for (String cardName : profile.getUnlockedCards()) {
                if (defaultDeck.size() == 5)
                    break;
                cardCatalog.getAllCards().stream()
                        .filter(c -> c.getName().equals(cardName))
                        .findFirst()
                        .ifPresent(defaultDeck::add);
            }
            customDeckRecipe = defaultDeck;
        }
        return new ArrayList<>(customDeckRecipe);
    }

    public Deck getCustomDeck(PlayerProfile profile) {
        return new Deck(getCustomDeckRecipe(profile));
    }

    public void clearCustomDeckRecipe() {
        this.customDeckRecipe = null;
    }

    /**
     * adapter che traduce le notifiche del dominio in eventi dell'application layer
     * questo permette al combatmanager di restare nel domain senza dipendere dagli eventi
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
}
