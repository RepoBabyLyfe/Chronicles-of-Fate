package it.unicam.cs.mpgc.rpg.matricola.domain;

public class EndPhaseState implements TurnState {

    @Override
    public void onEnter(CombatManager context) {
        System.out.println("[FASE DI FINE] Il turno dell'Eroe si conclude.");

        // AI del nemico
        // Se entrambi sono ancora vivi, il Boss sferra il suo attacco
        if (context.getEnemy().isAlive() && context.getPlayer().isAlive()) {
            System.out.println("--- TURNO DEL NEMICO ---");
            //TODO: Per ora gli facciamo fare un attacco fisso da 5 danni, da modificare in seguito
            context.getPlayer().takeDamage(5);
        }

        // Finito il turno del nemico, passiamo alla fase successiva (nuovo inizio)
        context.nextPhase();
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) {
        System.out.println("Azione negata: Il turno sta finendo.");
        return false;
    }

    @Override
    public void nextPhase(CombatManager context) {
        context.setState(new StartPhaseState());
    }
}