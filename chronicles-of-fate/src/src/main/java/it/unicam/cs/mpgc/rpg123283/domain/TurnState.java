package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * State Pattern: Definisce il contratto per le fasi del turno.
 * Ogni fase (Inizio, Azione, Fine) implementerà questa interfaccia.
 */
public interface TurnState {

    /**
     * Logica eseguita non appena si entra in questo stato.
     * @param context Il CombatManager che funge da contesto.
     */
    void onEnter(CombatManager context);

    /**
     * Tenta di giocare una carta nello stato corrente.
     * @param context Il CombatManager.
     * @param card La carta da giocare.
     * @param target Il bersaglio.
     * @return true se la giocata è permessa e avvenuta, false altrimenti.
     */
    boolean playCard(CombatManager context, Card card, Targetable target);

    /**
     * Richiede il passaggio alla fase successiva del turno.
     * @param context Il CombatManager.
     */
    void nextPhase(CombatManager context);
}