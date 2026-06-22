package it.unicam.cs.mpgc.rpg.matricola.domain;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.GameEvent;

/**
 * Aggregate Root: Gestisce il combattimento e mantiene lo stato del turno.
 * L'esecuzione delle regole è delegata all'oggetto TurnState corrente.
 */
public class CombatManager {

    private final Character player;
    private final Character enemy;
    private TurnState currentState;
    private int lastDiceRoll = 0;
    private final BossAI bossAI;
    private String lastEnemyAction = "Il nemico si muove nell'oscurità...";

    // Bus degli Eventi Iniettato
    private final EventPublisher eventBus;

    /**
     * Costruttore con archetipo specifico.
     */
    public CombatManager(Character player, Character enemy, EventPublisher eventBus, EnemyArchetype archetype) {
        if (player == null || enemy == null || eventBus == null) {
            throw new IllegalArgumentException("Parametri non validi.");
        }
        this.player = player;
        this.enemy = enemy;
        this.eventBus = eventBus;
        this.bossAI = new BossAI(archetype);
    }

    /**
     * Costruttore retrocompatibile: usa l'Avatar dell'Entropia di default.
     */
    public CombatManager(Character player, Character enemy, EventPublisher eventBus) {
        this(player, enemy, eventBus, EnemyArchetype.ENTROPY_AVATAR);
    }

    public void publishEvent(GameEvent event) {
        eventBus.publish(event);
    }

    // --- Metodi di Combattimento ---
    public void startCombat() {
        System.out.println("--- INIZIO COMBATTIMENTO ---");
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