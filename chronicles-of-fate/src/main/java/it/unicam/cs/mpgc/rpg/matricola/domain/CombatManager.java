package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Aggregate Root: Gestisce il combattimento e mantiene lo stato del turno.
 * L'esecuzione delle regole è delegata all'oggetto TurnState corrente.
 */
public class CombatManager {

    private final Character player;
    private final Character enemy;
    private TurnState currentState;

    public CombatManager(Character player, Character enemy) {
        if (player == null || enemy == null) {
            throw new IllegalArgumentException("I partecipanti non possono essere nulli.");
        }
        this.player = player;
        this.enemy = enemy;
    }

    /**
     * Inizia il combattimento impostando il primo stato.
     */
    public void startCombat() {
        System.out.println("--- INIZIO COMBATTIMENTO ---");
        setState(new StartPhaseState());
    }

    /**
     * Cambia lo stato corrente ed esegue la logica di ingresso.
     */
    protected void setState(TurnState newState) {
        this.currentState = newState;
        this.currentState.onEnter(this);
    }

    // --- Metodi delegati allo Stato Corrente ---

    public boolean playCard(Card card, Targetable target) {
        if (currentState == null) throw new IllegalStateException("Combattimento non avviato.");
        return currentState.playCard(this, card, target);
    }

    public void nextPhase() {
        if (currentState == null) throw new IllegalStateException("Combattimento non avviato.");
        currentState.nextPhase(this);
    }

    // --- Getters ---
    public Character getPlayer() { return player; }
    public Character getEnemy() { return enemy; }
}