package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.logging.Logger;

/**
 * Aggregate Root: Gestisce il combattimento e mantiene lo stato del turno.
 * L'esecuzione delle regole è delegata all'oggetto TurnState corrente.
 */
public class CombatManager {

    private static final Logger LOGGER = Logger.getLogger(CombatManager.class.getName());

    private final Character player;
    private final Character enemy;
    private TurnState currentState;
    private int lastDiceRoll = 0;
    private final BossAI bossAI;
    private String lastEnemyAction = "Il nemico si muove nell'oscurità...";

    private final CombatEventListener eventListener;

    /**
     * Costruttore con archetipo specifico.
     */
    public CombatManager(Character player, Character enemy, CombatEventListener eventListener, EnemyArchetype archetype) {
        if (player == null || enemy == null || eventListener == null) {
            throw new IllegalArgumentException("Parametri non validi.");
        }
        this.player = player;
        this.enemy = enemy;
        this.eventListener = eventListener;
        this.bossAI = new BossAI(archetype);
    }

    /**
     * Costruttore retrocompatibile: usa l'Avatar dell'Entropia di default.
     */
    public CombatManager(Character player, Character enemy, CombatEventListener eventListener) {
        this(player, enemy, eventListener, EnemyArchetype.ENTROPY_AVATAR);
    }

    // --- Notifiche verso l'esterno (usate dagli stati del turno) ---

    public void notifyEnemyCardPlayed(String cardName, String logMessage, String imagePath) {
        eventListener.onEnemyCardPlayed(cardName, logMessage, imagePath);
    }

    public void notifyLog(String message) {
        eventListener.onLogMessage(message);
    }

    public void notifyDamageTaken(Character target, int damageAmount, boolean isCritical) {
        eventListener.onDamageTaken(target, damageAmount, isCritical);
    }

    // --- Metodi di Combattimento ---
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

    // --- Getters & Setters ---
    public void setLastDiceRoll(int roll) { this.lastDiceRoll = roll; }
    public int getLastDiceRoll() { return lastDiceRoll; }
    public BossAI getBossAI() { return bossAI; }
    public String getLastEnemyAction() { return lastEnemyAction; }
    public void setLastEnemyAction(String lastEnemyAction) { this.lastEnemyAction = lastEnemyAction; }
    public Character getPlayer() { return player; }
    public Character getEnemy() { return enemy; }
}