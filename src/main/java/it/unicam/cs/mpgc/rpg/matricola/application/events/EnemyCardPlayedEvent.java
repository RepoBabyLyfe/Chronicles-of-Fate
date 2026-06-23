package it.unicam.cs.mpgc.rpg.matricola.application.events;

public record EnemyCardPlayedEvent(String cardName, String logMessage, String imagePath) implements GameEvent {}