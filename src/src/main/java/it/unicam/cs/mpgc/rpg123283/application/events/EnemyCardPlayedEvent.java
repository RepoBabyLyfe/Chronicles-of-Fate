package it.unicam.cs.mpgc.rpg123283.application.events;

public record EnemyCardPlayedEvent(String cardName, String logMessage, String imagePath) implements GameEvent {}