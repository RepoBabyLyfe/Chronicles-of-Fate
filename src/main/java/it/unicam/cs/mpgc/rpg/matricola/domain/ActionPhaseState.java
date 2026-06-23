package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.logging.Logger;

public class ActionPhaseState implements TurnState {
    private static final Logger LOGGER = Logger.getLogger(ActionPhaseState.class.getName());

    @Override
    public void onEnter(CombatManager context) {
        LOGGER.info("[FASE DI AZIONE] In attesa delle mosse del giocatore...");
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) {
        Character player = context.getPlayer();

        // 1. Controllo Focus
        if (!player.consumeFocus(card.getManaCost())) {
            LOGGER.warning("Focus insufficiente per giocare " + card.getName());
            return false;
        }

        // 2. Risoluzione (Dado opzionale)
        RollResult esitoDado = null;

        if (card.requiresDice()) {
            esitoDado = card.getRequiredDice().roll();
            context.setLastDiceRoll(esitoDado.value());
            LOGGER.info("Giocata: [" + card.getName() + "] -> Dado: " + esitoDado.value());
        } else {
            context.setLastDiceRoll(0); // Flag per indicare assenza di lancio
            LOGGER.info("Giocata: [" + card.getName() + "] -> Effetto Istantaneo!");
        }

        // RIGA CORRETTA: Passiamo player (sorgente), target (bersaglio) ed esitoDado
        card.getEffect().apply(player, target, esitoDado);
        return true;
    }

    @Override
    public void nextPhase(CombatManager context) {
        context.setState(new EndPhaseState());
    }
}