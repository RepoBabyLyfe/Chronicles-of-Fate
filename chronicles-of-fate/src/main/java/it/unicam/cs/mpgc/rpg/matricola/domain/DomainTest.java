package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.List;
import java.util.random.RandomGenerator;

public class DomainTest {

    public static void main(String[] args) {
        System.out.println("--- AVVIO TEST: COMBAT MANAGER E STATE PATTERN ---\n");

        // 1. Creazione dei personaggi
        Character eroe = new Character("Tessitore di Fati", 30, 5) {};
        Character nemico = new Character("Avatar dell'Entropia", 50, 0) {};

        // 2. Creazione dell'Arbitro (CombatManager)
        CombatManager manager = new CombatManager(eroe, nemico);

        // 3. Preparazione della Carta "Colpo Instabile"
        Rollable d6 = new StandardDice(6, RandomGenerator.getDefault());
        List<DamageTier> fasceColpoInstabile = List.of(
                new DamageTier(1, 2, 2),
                new DamageTier(3, 5, 6),
                new DamageTier(6, 6, 12)
        );
        CardEffect effettoInstabile = new TieredDamageEffect(fasceColpoInstabile);
        Card colpoInstabile = new Card("Colpo Instabile", 1, d6, effettoInstabile);

        // 4. AVVIO DEL COMBATTIMENTO
        // Questo innescherà la StartPhase (che ripristina Focus) e passerà subito alla ActionPhase
        manager.startCombat();

        System.out.println("\nStato pre-giocata:");
        System.out.println("Focus Eroe: " + eroe.getCurrentFocus());
        System.out.println("HP Nemico: " + nemico.getCurrentHp());
        System.out.println("----------------------------------------");

        // 5. Giochiamo la carta DELEGANDO al CombatManager
        System.out.println("Tento di giocare: [" + colpoInstabile.getName() + "]");
        boolean successo = manager.playCard(colpoInstabile, nemico);

        if (successo) {
            System.out.println("=> Carta giocata e validata dall'Arbitro!");
        } else {
            System.out.println("=> Giocata fallita (Focus insufficiente o Fase errata).");
        }

        System.out.println("----------------------------------------");
        System.out.println("Stato post-giocata:");
        System.out.println("Focus Eroe residuo: " + eroe.getCurrentFocus());
        System.out.println("HP Nemico finali: " + nemico.getCurrentHp() + "/" + nemico.getMaxHp());

        // 6. Fine del turno
        System.out.println("\nL'eroe decide di passare il turno...");
        manager.nextPhase();
    }
}