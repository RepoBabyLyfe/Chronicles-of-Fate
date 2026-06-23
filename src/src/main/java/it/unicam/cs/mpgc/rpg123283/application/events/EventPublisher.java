package it.unicam.cs.mpgc.rpg123283.application.events;

//contratto per il sistema di pubblicazione degli eventi
public interface EventPublisher {

    //pubblica un evento affinché i subscriber possano reagire
    void publish(GameEvent event);
}