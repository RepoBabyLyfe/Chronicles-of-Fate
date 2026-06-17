package it.unicam.cs.mpgc.rpg.matricola.domain;

public class ActionPhaseState implements TurnState {

    @Override
    public void onEnter(CombatManager context) {
        System.out.println("[FASE DI AZIONE] In attesa delle mosse del giocatore...");
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) {
        Character player = context.getPlayer();

        // 1. Controllo Focus
        if (!player.consumeFocus(card.getManaCost())) {
            System.out.println("Focus insufficiente per giocare " + card.getName());
            return false;
        }

        // 2. Risoluzione della giocata (Dado + Effetto)
        RollResult esitoDado = card.getRequiredDice().roll();
        context.setLastDiceRoll(esitoDado.value());
        System.out.println("Giocata: [" + card.getName() + "] -> Dado: " + esitoDado.value());

        card.getEffect().apply(target, esitoDado);
        return true;
    }

    @Override
    public void nextPhase(CombatManager context) {
        context.setState(new EndPhaseState());
    }
}