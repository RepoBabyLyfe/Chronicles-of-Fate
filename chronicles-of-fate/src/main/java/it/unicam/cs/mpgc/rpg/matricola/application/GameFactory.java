package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class GameFactory {

    public static Character createPlayer() {
        return new Character("Tessitore di Fati", 30, 10) {};
    }

    public static Character createEnemy() {
        return new Character("Avatar dell'Entropia", 50, 0) {};
    }

    public static Deck createStartingDeck() {
        Rollable d6 = new StandardDice(6, java.util.random.RandomGenerator.getDefault());

        // 1. Risonanza Eterea (Con immagine personalizzata passata come 5° parametro!)
        // 1. Risonanza Eterea (Attenzione al .jpg finale!)
        Card risonanza = new Card("Risonanza Eterea", 1, d6, (source, target, roll) -> {
            source.restoreFocus(roll.value());
        }, "/images/risonanza_eterea.png");

        // 2. Fenditura Quantica (Attacco Standard: Danno base + dado)
        Card fenditura = new Card("Fenditura Quantica", 2, d6, (source, target, roll) -> {
            target.takeDamage(3 + roll.value());
        }, "/images/fenditura_quantica.png");

        // 3. Sovraccarico Biologico (Rischio estremo: danni doppi ma ferisce l'eroe)
        Card sovraccarico = new Card("Sovraccarico Biologico", 4, d6, (source, target, roll) -> {
            target.takeDamage(roll.value() * 2);
            source.takeDamage(2); // Rinculo!
        }, "/images/sovraccarico_biologico.png");

        // 4. Innesto Simbiotico (Sopravvivenza: cura l'eroe)
        Card innesto = new Card("Innesto Simbiotico", 3, d6, (source, target, roll) -> {
            source.heal(roll.value() + 1);
        }, "/images/innesto_simbiotico.png");

        // 5. Assimilazione Oscura (Sacrificio: scambia Vita per Focus, il dado viene ignorato)
        Card assimilazione = new Card("Assimilazione Oscura", 0, null, (source, target, roll) -> {
            source.takeDamage(3);
            source.restoreFocus(4);
        }, "/images/assimilazione_oscura.png");

        // Assembliamo il mazzo iniziale (inseriamo 2 copie per ciascuna carta per arrivare a 10 carte totali)
        List<Card> initialCards = new java.util.ArrayList<>();
        for(int i = 0; i < 2; i++) {
            initialCards.add(risonanza);
            initialCards.add(fenditura);
            initialCards.add(sovraccarico);
            initialCards.add(innesto);
            initialCards.add(assimilazione);
        }

        return new Deck(initialCards);
    }
}