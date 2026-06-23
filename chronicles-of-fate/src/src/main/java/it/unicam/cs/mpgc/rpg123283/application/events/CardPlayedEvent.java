package it.unicam.cs.mpgc.rpg123283.application.events;

import it.unicam.cs.mpgc.rpg123283.domain.Card;
import it.unicam.cs.mpgc.rpg123283.domain.Targetable;

//evento emesso quando una carta viene giocata con successo
public record CardPlayedEvent(Card card, Targetable target, int diceResult) implements GameEvent {
}