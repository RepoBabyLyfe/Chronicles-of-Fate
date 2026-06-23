package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Gestisce il grimorio del giocatore: mazzo, mano e pila degli scarti.
 */
public class Deck {
    private static final Logger LOGGER = Logger.getLogger(Deck.class.getName());

    private final List<Card> drawPile;
    private final List<Card> hand;
    private final List<Card> discardPile;

    public Deck(List<Card> initialCards) {
        this.drawPile = new ArrayList<>(initialCards);
        this.hand = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        Collections.shuffle(this.drawPile); // Mescola all'inizio
    }

    public void drawCards(int count) {
        for (int i = 0; i < count; i++) {
            // Se il mazzo è vuoto, rimescola gli scarti
            if (drawPile.isEmpty()) {
                if (discardPile.isEmpty()) return; // Niente da pescare
                drawPile.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(drawPile);
                LOGGER.info("Il grimorio è stato rimescolato.");
            }
            hand.add(drawPile.remove(0));
        }
    }

    public void discardCard(Card card) {
        hand.remove(card);
        discardPile.add(card);
    }

    public void discardHand() {
        discardPile.addAll(hand);
        hand.clear();
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }
}