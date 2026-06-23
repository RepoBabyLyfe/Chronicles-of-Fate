package it.unicam.cs.mpgc.rpg.matricola.application.events;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;

public record DamageTakenEvent(Character target, int damageAmount, boolean isCritical) implements GameEvent {}