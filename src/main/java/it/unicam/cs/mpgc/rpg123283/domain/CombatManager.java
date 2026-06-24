package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.logging.Logger;

/**
 * Gestisce il combattimento e mantiene lo stato del turno
 * l'esecuzione delle regole è delegata all'oggetto TurnState corrente
 */
public class CombatManager {

    private static final Logger LOGGER = Logger.getLogger(CombatManager.class.getName());

    private final Character player;
    private final Character enemy;
    private TurnState currentState;
    private int lastDiceRoll = 0;
    private IEnemyAI bossAI;
    private String lastEnemyAction = "Il nemico si muove nell'oscurità...";

    private final CombatEventListener eventListener;

    public CombatManager(Character player, Character enemy, CombatEventListener eventListener, IEnemyAI bossAI) {
        if (player == null || enemy == null || eventListener == null || bossAI == null) {
            throw new IllegalArgumentException("Parametri non validi.");
        }
        this.player = player;
        this.enemy = enemy;
        this.eventListener = eventListener;
        this.bossAI = bossAI;
    }

    public CombatManager(Character player, Character enemy, CombatEventListener eventListener) {
        this(player, enemy, eventListener, new BossAI(EnemyArchetype.ENTROPY_AVATAR));
    }

    //notifiche verso l'esterno

    public void notifyEnemyCardPlayed(String cardName, String logMessage, String imagePath) {
        eventListener.onEnemyCardPlayed(cardName, logMessage, imagePath);
    }

    public void notifyLog(String message) {
        eventListener.onLogMessage(message);
    }

    public void notifyDamageTaken(Character target, int damageAmount, boolean isCritical) {
        eventListener.onDamageTaken(target, damageAmount, isCritical);
    }

    //metodi di combattimento
    public void startCombat() {
        LOGGER.info("--- INIZIO COMBATTIMENTO ---");
        setState(new StartPhaseState());
    }

    protected void setState(TurnState newState) {
        this.currentState = newState;
        this.currentState.onEnter(this);
    }

    public boolean playCard(Card card, Targetable target) {
        if (currentState == null) throw new IllegalStateException("Combattimento non avviato.");
        return currentState.playCard(this, card, target);
    }

    public void nextPhase() {
        if (currentState == null) throw new IllegalStateException("Combattimento non avviato.");
        currentState.nextPhase(this);
    }

    public void setLastDiceRoll(int roll) { this.lastDiceRoll = roll; }
    public int getLastDiceRoll() { return lastDiceRoll; }
    public IEnemyAI getBossAI() { return bossAI; }
    public String getLastEnemyAction() { return lastEnemyAction; }
    public void setLastEnemyAction(String lastEnemyAction) { this.lastEnemyAction = lastEnemyAction; }
    public Character getPlayer() { return player; }
    public Character getEnemy() { return enemy; }
}