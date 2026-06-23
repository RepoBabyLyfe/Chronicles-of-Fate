package it.unicam.cs.mpgc.rpg123283.application.events;

import it.unicam.cs.mpgc.rpg123283.domain.Character;

//evento emesso quando i punti salute di un personaggio cambiano
public record HpChangedEvent(Character character, int currentHp, int maxHp) implements GameEvent {
}