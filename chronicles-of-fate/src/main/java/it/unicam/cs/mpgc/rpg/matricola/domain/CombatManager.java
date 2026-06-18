package it.unicam.cs.mpgc.rpg.matricola.domain;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.GameEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: Gestisce il combattimento e mantiene lo stato del turno.
 * L'esecuzione delle regole è delegata all'oggetto TurnState corrente.
 */
public class CombatManager {

    private final Character player;
    private final Character enemy;
    private TurnState currentState;
    private int lastDiceRoll = 0;
    private final EntropyAI enemyAI = new EntropyAI();
    private String lastEnemyAction = "L'Avatar dell'Entropia si muove nell'oscurità...";

    // --- NUOVO: Bus degli Eventi ---
    private final List<EventPublisher> subscribers = new ArrayList<>();

    public CombatManager(Character player, Character enemy) {
        if (player == null || enemy == null) {
            throw new IllegalArgumentException("I partecipanti non possono essere nulli.");
        }
        this.player = player;
        this.enemy = enemy;
    }

    public void subscribe(EventPublisher subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public void publishEvent(GameEvent event) {
        for (EventPublisher sub : subscribers) {
            sub.publish(event);
        }
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
    public EntropyAI getEnemyAI() { return enemyAI; }
    public String getLastEnemyAction() { return lastEnemyAction; }
    public void setLastEnemyAction(String lastEnemyAction) { this.lastEnemyAction = lastEnemyAction; }
    public Character getPlayer() { return player; }
    public Character getEnemy() { return enemy; }
}