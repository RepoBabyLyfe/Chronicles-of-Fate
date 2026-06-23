package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.GameEvent;
import it.unicam.cs.mpgc.rpg.matricola.application.events.HpChangedEvent;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.persistence.JsonGameStateRepository;

public class EventSystemTest {

    public static void main(String[] args) {
        System.out.println("--- TEST SISTEMA AD EVENTI ---");

        // 1. Creiamo un finto Publisher per il test
        EventPublisher consolePublisher = new EventPublisher() {
            @Override
            public void publish(GameEvent event) {
                // Quando intercettiamo un evento, controlliamo di che tipo è
                if (event instanceof HpChangedEvent hpEvent) {
                    System.out.println(" 📻 [RADIO BROADCAST] Aggiornamento UI: La barra della vita di "
                            + hpEvent.character().getName() + " va aggiornata a "
                            + hpEvent.currentHp() + "/" + hpEvent.maxHp());
                }
            }
        };

        // 2. Assembliamo il Service con il repository e il nostro finto Publisher
        GameStateRepository repo = new JsonGameStateRepository("savegame.json");
        GameService service = new GameService(repo, consolePublisher);

        // 3. Avviamo la partita
        Character eroe = new Character("Tessitore di Fati", 30, 5) {};
        Character nemico = new Character("Avatar dell'Entropia", 50, 0) {};

        System.out.println("\nAvvio nuova partita...");
        service.startNewGame(eroe, nemico);

        System.out.println("\nCaricamento partita precedente...");
        service.loadGame(eroe, nemico);
    }
}