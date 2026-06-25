package it.unicam.cs.mpgc.rpg123283.domain;

//state pattern: definisce il contratto per le fasi del turno
//ogni fase (Inizio, Azione, Fine) implementerà questa interfaccia
public interface TurnState {

    /**
     * Logica eseguita non appena si entra in questo stato
     * @param context Il CombatManager che funge da contesto
     */
    void onEnter(CombatManager context);

    /**
     * tenta di giocare una carta nello stato corrente
     * @param context il CombatManager
     * @param card la carta da giocare
     * @param target il bersaglio
     * @return true se la giocata è permessa e avvenuta, false altrimenti
     */
    boolean playCard(CombatManager context, Card card, Targetable target);

    /**
     * richiede il passaggio alla fase successiva del turno
     * @param context il CombatManager
     */
    void nextPhase(CombatManager context);
}