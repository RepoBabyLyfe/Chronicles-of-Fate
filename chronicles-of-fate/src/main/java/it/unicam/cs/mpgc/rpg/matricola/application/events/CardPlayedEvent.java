package it.unicam.cs.mpgc.rpg.matricola.application.events;

import it.unicam.cs.mpgc.rpg.matricola.domain.Card;
import it.unicam.cs.mpgc.rpg.matricola.domain.Targetable;

/**
 * Evento emesso quando una carta viene giocata con successo.
 */
public record CardPlayedEvent(Card card, Targetable target, int diceResult) implements GameEvent {
}