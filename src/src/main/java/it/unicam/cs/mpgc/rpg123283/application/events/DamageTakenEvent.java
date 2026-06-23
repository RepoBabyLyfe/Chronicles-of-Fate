package it.unicam.cs.mpgc.rpg123283.application.events;
import it.unicam.cs.mpgc.rpg123283.domain.Character;

public record DamageTakenEvent(Character target, int damageAmount, boolean isCritical) implements GameEvent {}