package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.logging.Logger;

public class StartPhaseState implements TurnState {
    private static final Logger LOGGER = Logger.getLogger(StartPhaseState.class.getName());

    @Override
    public void onEnter(CombatManager context) {
        LOGGER.info("[FASE DI INIZIO] L'eroe recupera Focus e si prepara.");
        // Ripristina, ad esempio, 3 Focus all'inizio di ogni turno
        context.getPlayer().restoreFocus(3);
        context.nextPhase();
    }

    @Override
    public boolean playCard(CombatManager context, Card card, Targetable target) {
        LOGGER.warning("Azione negata: Impossibile giocare carte nella Fase di Inizio.");
        return false;
    }

    @Override
    public void nextPhase(CombatManager context) {
        context.setState(new ActionPhaseState());
    }
}