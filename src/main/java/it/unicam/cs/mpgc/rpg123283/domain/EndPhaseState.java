package it.unicam.cs.mpgc.rpg123283.domain;

//fase di fine turno: il boss esegue la sua mossa tramite l'IA.
//le notifiche verso l'esterno sono delegate al CombatManager tramite CombatEventListener.
public class EndPhaseState implements TurnState {

    @Override
    public void onEnter(CombatManager context) {
        if (context.getEnemy().isAlive() && context.getPlayer().isAlive()) {
            int hpPrima = context.getPlayer().getCurrentHp();

            BossMove move = context.getBossAI().takeTurn(context.getEnemy(), context.getPlayer());
            context.setLastEnemyAction(move.logMessage());

            int dannoFatto = hpPrima - context.getPlayer().getCurrentHp();

            context.notifyEnemyCardPlayed(move.name(), move.logMessage(), move.imagePath());
            context.notifyLog("[BOSS] " + move.logMessage());

            if (dannoFatto > 0) {
                context.notifyDamageTaken(context.getPlayer(), dannoFatto, false);
            }
        }
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) { return false; }
    @Override
    public void nextPhase(CombatManager context) { context.setState(new StartPhaseState()); }
}