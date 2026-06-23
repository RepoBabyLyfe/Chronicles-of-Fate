package it.unicam.cs.mpgc.rpg123283.infrastructure;

import it.unicam.cs.mpgc.rpg123283.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg123283.application.events.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * gestore centralizzato degli eventi di gioco
 * disaccoppia chi emette gli eventi dominio/service da chi li ascolta ui/logger
 */
public class GameEventBus implements EventPublisher {

    private static GameEventBus instance;
    private final List<EventPublisher> subscribers = new ArrayList<>();

    private GameEventBus() {}

    public static GameEventBus getInstance() {
        if (instance == null) {
            instance = new GameEventBus();
        }
        return instance;
    }

    public void subscribe(EventPublisher subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public void unsubscribe(EventPublisher subscriber) {
        subscribers.remove(subscriber);
    }

    public void clearSubscribers() {
        subscribers.clear();
    }

    @Override
    public void publish(GameEvent event) {
        //invia l'evento a tutti i sottoscrittori attivi
        for (EventPublisher subscriber : new ArrayList<>(subscribers)) {
            subscriber.publish(event);
        }
    }
}