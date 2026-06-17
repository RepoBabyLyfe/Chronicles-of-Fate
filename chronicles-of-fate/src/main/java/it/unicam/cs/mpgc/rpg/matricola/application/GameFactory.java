package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class GameFactory {

    public static Character createPlayer() {
        return new Character("Tessitore di Fati", 30, 5) {};
    }

    public static Character createEnemy() {
        return new Character("Avatar dell'Entropia", 50, 0) {};
    }

    public static Deck createStartingDeck() {
        Rollable d6 = new StandardDice(6, RandomGenerator.getDefault());

        CardEffect effettoInstabile = new TieredDamageEffect(List.of(
                new DamageTier(1, 2, 2), new DamageTier(3, 5, 6), new DamageTier(6, 6, 12)
        ));
        Card colpoInstabile = new Card("Colpo Instabile", 1, d6, effettoInstabile);

        CardEffect effettoLieve = new TieredDamageEffect(List.of(
                new DamageTier(1, 6, 4)
        ));
        Card colpoSicuro = new Card("Colpo Sicuro", 2, d6, effettoLieve);

        List<Card> initialCards = new ArrayList<>();
        for(int i = 0; i < 7; i++) initialCards.add(colpoInstabile);
        for(int i = 0; i < 3; i++) initialCards.add(colpoSicuro);

        return new Deck(initialCards);
    }
}