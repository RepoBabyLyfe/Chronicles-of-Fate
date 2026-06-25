package it.unicam.cs.mpgc.rpg123283.application.events;

/**
 *contratto per il sistema di pubblicazione degli eventi (observer pattern)
 */
public interface EventPublisher {
    void publish(GameEvent event);
}