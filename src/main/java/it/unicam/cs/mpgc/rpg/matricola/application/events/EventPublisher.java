package it.unicam.cs.mpgc.rpg.matricola.application.events;

/**
 * Contratto per il sistema di pubblicazione degli eventi (Observer Pattern).
 */
public interface EventPublisher {

    /**
     * Pubblica un evento affinché i subscriber (es. la GUI) possano reagire.
     */
    void publish(GameEvent event);
}