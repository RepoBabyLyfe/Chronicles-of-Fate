package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.GameEvent;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.persistence.JsonGameStateRepository;

public class ApplicationTest {

    public static void main(String[] args) {
        System.out.println("--- AVVIO TEST COMPLETO: SALVATAGGIO JSON E EVENTI ---\n");

        // 1. Creiamo un finto Publisher per soddisfare il costruttore del Service
        EventPublisher dummyPublisher = new EventPublisher() {
            @Override
            public void publish(GameEvent event) {
                System.out.println("  📻 [EVENTO TRASMESSO] -> " + event.getClass().getSimpleName());
            }
        };

        // 2. Creiamo Repo e iniettiamo tutto nel Service
        GameStateRepository repo = new JsonGameStateRepository("savegame.json");
        GameService gameService = new GameService(repo, dummyPublisher);

        System.out.println("=== SCENARIO 1: NUOVA PARTITA E SALVATAGGIO ===");
        Character eroe = new Character("Tessitore di Fati", 30, 5) {};
        Character nemico = new Character("Avatar dell'Entropia", 50, 0) {};

        // Questo innescherà la stampa degli eventi HpChangedEvent
        gameService.startNewGame(eroe, nemico);

        System.out.println("\nL'eroe infligge 15 danni al nemico...");
        gameService.getCombatManager().getEnemy().takeDamage(15);
        System.out.println("HP Nemico attuali: " + gameService.getCombatManager().getEnemy().getCurrentHp());

        System.out.println("\nEsecuzione salvataggio su file...");
        gameService.saveGame();


        System.out.println("\n=== SCENARIO 2: CARICAMENTO DELLA PARTITA ===");
        Character nuovoEroe = new Character("Tessitore di Fati", 30, 5) {};
        Character nuovoNemico = new Character("Avatar dell'Entropia", 50, 0) {};

        System.out.println("Stato pre-caricamento -> HP Nemico: " + nuovoNemico.getCurrentHp());

        // Carichiamo la partita (innescherà di nuovo gli eventi per aggiornare la UI)
        gameService.loadGame(nuovoEroe, nuovoNemico);

        System.out.println("Stato post-caricamento -> HP Nemico: " + gameService.getCombatManager().getEnemy().getCurrentHp());

        if (gameService.getCombatManager().getEnemy().getCurrentHp() == 35) {
            System.out.println("\n✅ TEST SUPERATO: Il file JSON ha persistito correttamente lo stato!");
        } else {
            System.out.println("\n❌ ERRORE: I dati caricati non corrispondono.");
        }
    }
}