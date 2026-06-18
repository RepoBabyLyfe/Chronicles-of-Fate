package it.unicam.cs.mpgc.rpg.matricola.domain;

import it.unicam.cs.mpgc.rpg.matricola.application.events.LogEvent;
import it.unicam.cs.mpgc.rpg.matricola.application.events.DamageTakenEvent;

public class EndPhaseState implements TurnState {

    @Override
    public void onEnter(CombatManager context) {
        System.out.println("[FASE DI FINE] Il turno dell'Eroe si conclude.");

        if (context.getEnemy().isAlive() && context.getPlayer().isAlive()) {
            System.out.println("--- TURNO DELL'AVATAR ALIENOCENTRICO ---");

            int hpPrima = context.getPlayer().getCurrentHp();
            String actionLog = context.getEnemyAI().takeTurn(context.getEnemy(), context.getPlayer());
            context.setLastEnemyAction(actionLog);

            int dannoFatto = hpPrima - context.getPlayer().getCurrentHp();

            // IL DOMINIO COMUNICA CON LA UI TRAMITE EVENTI!
            context.publishEvent(new LogEvent("👽 " + actionLog));
            if (dannoFatto > 0) {
                context.publishEvent(new DamageTakenEvent(context.getPlayer(), dannoFatto, false));
            }
        }

        context.nextPhase();
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) {
        System.out.println("Non puoi giocare carte nella fase finale.");
        return false;
    }

    @Override
    public void nextPhase(CombatManager context) {
        context.setState(new StartPhaseState());
    }
}