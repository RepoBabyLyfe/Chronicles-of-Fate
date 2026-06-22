package it.unicam.cs.mpgc.rpg.matricola.domain;

import it.unicam.cs.mpgc.rpg.matricola.application.events.LogEvent;
import it.unicam.cs.mpgc.rpg.matricola.application.events.DamageTakenEvent;
import it.unicam.cs.mpgc.rpg.matricola.application.events.EnemyCardPlayedEvent;

public class EndPhaseState implements TurnState {

    @Override
    public void onEnter(CombatManager context) {
        if (context.getEnemy().isAlive() && context.getPlayer().isAlive()) {
            int hpPrima = context.getPlayer().getCurrentHp();

            BossMove move = context.getBossAI().takeTurn(context.getEnemy(), context.getPlayer());
            context.setLastEnemyAction(move.logMessage());

            int dannoFatto = hpPrima - context.getPlayer().getCurrentHp();

            context.publishEvent(new EnemyCardPlayedEvent(move.name(), move.logMessage(), move.imagePath()));
            context.publishEvent(new LogEvent("[BOSS] " + move.logMessage()));

            if (dannoFatto > 0) {
                context.publishEvent(new DamageTakenEvent(context.getPlayer(), dannoFatto, false));
            }
        }
        context.nextPhase();
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) { return false; }
    @Override
    public void nextPhase(CombatManager context) { context.setState(new StartPhaseState()); }
}