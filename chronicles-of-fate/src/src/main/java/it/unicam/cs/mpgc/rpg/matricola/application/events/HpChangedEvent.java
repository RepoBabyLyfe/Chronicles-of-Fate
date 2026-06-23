package it.unicam.cs.mpgc.rpg.matricola.application.events;

import it.unicam.cs.mpgc.rpg.matricola.domain.Character;

/**
 * Evento emesso quando i Punti Salute di un personaggio cambiano.
 */
public record HpChangedEvent(Character character, int currentHp, int maxHp) implements GameEvent {
}