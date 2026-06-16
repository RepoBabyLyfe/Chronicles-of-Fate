package it.unicam.cs.mpgc.rpg.matricola.domain;

public class EndPhaseState implements TurnState {

    @Override
    public void onEnter(CombatManager context) {
        System.out.println("[FASE DI FINE] Il turno dell'Eroe si conclude.");
        // Qui in futuro si scarteranno le carte rimaste in mano

        // Passaggio logico del turno al nemico (Simulato per ora)
        System.out.println("--- TURNO DEL NEMICO (IA in sviluppo) ---");

        // Dopo il turno del nemico, si ricomincia
        context.nextPhase();
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) {
        System.out.println("Azione negata: Il turno sta finendo.");
        return false;
    }

    @Override
    public void nextPhase(CombatManager context) {
        System.out.println("Ritorno alla Fase di Inizio...\n");
        context.setState(new StartPhaseState());
    }
}