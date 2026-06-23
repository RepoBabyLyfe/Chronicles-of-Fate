package it.unicam.cs.mpgc.rpg.matricola.infrastructure;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestore centralizzato degli eventi di gioco.
 * Disaccoppia chi emette gli eventi (Dominio/Service) da chi li ascolta (UI/Logger).
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
        // Invia l'evento a tutti i sottoscrittori attivi
        for (EventPublisher subscriber : new ArrayList<>(subscribers)) {
            subscriber.publish(event);
        }
    }
}