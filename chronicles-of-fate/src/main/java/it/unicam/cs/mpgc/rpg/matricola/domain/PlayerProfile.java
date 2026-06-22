package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerProfile {

    private int etherFragments;
    private final Set<String> unlockedCardNames;

    public PlayerProfile(int initialFragments, Set<String> initialCards) {
        this.etherFragments = initialFragments;
        // L'HashSet impedisce in automatico che ci siano nomi duplicati
        this.unlockedCardNames = new HashSet<>(initialCards);
    }

    public int getEtherFragments() {
        return etherFragments;
    }

    public void addFragments(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Impossibile aggiungere frammenti negativi.");
        this.etherFragments += amount;
    }

    public boolean spendFragments(int amount) {
        if (amount < 0 || this.etherFragments < amount) {
            return false; // Fondi insufficienti
        }
        this.etherFragments -= amount;
        return true;
    }

    public Set<String> getUnlockedCards() {
        return Collections.unmodifiableSet(unlockedCardNames);
    }

    public void unlockCard(String cardName) {
        this.unlockedCardNames.add(cardName);
    }

    public boolean hasCard(String cardName) {
        return this.unlockedCardNames.contains(cardName);
    }
}