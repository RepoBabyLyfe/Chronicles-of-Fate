package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Oggetto di Dominio che incapsula la logica e i vincoli di creazione di un mazzo.
 */
public class DeckBuilder {

    private static final int MAX_CARDS = 5;
    private final List<Card> selectedCards = new ArrayList<>();

    /**
     * Tenta di aggiungere una carta al mazzo in costruzione.
     * @return true se aggiunta con successo, false se il mazzo è già pieno.
     */
    public boolean addCard(Card card) {
        //rifiuta se il mazzo è pieno
        if (selectedCards.size() >= MAX_CARDS) {
            return false;
        }
        //rifiuta se la carta è già stata selezionata
        if (selectedCards.contains(card)) {
            return false;
        }

        selectedCards.add(card);
        return true;
    }

    public void removeCard(Card card) {
        selectedCards.remove(card);
    }

    public boolean isComplete() {
        return selectedCards.size() == MAX_CARDS;
    }

    /**
     * Calcola il costo medio in Focus delle carte attualmente selezionate.
     */
    public double getAverageFocusCost() {
        if (selectedCards.isEmpty()) return 0.0;

        double totalCost = 0;
        for (Card c : selectedCards) {
            totalCost += c.getManaCost();
        }
        //arrotonda a 1 decimale per visualizzare nell'HUD
        return Math.round((totalCost / selectedCards.size()) * 10.0) / 10.0;
    }

    public List<Card> getSelectedCards() {
        return Collections.unmodifiableList(selectedCards);
    }

    // genera l'oggetto deck definitivo pronto per il combatmanager
    public Deck build() {
        if (!isComplete()) {
            throw new IllegalStateException("Il mazzo non è completo. Servono esattamente " + MAX_CARDS + " carte.");
        }
        //passiamo una copia della lista per evitare effetti collaterali
        return new Deck(new ArrayList<>(selectedCards));
    }
}