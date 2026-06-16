package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Entità che rappresenta una Carta del Grimorio.
 * Rispetta il Single Responsibility Principle: delega l'applicazione delle regole al suo CardEffect.
 */
public class Card {

    private final String name;
    private final int manaCost;
    private final Rollable requiredDice;
    private final CardEffect effect;

    public Card(String name, int manaCost, Rollable requiredDice, CardEffect effect) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della carta non può essere vuoto.");
        }
        if (manaCost < 0) {
            throw new IllegalArgumentException("Il costo in Focus non può essere negativo.");
        }
        if (requiredDice == null || effect == null) {
            throw new IllegalArgumentException("Dado richiesto ed effetto non possono essere nulli.");
        }

        this.name = name;
        this.manaCost = manaCost;
        this.requiredDice = requiredDice;
        this.effect = effect;
    }

    public String getName() { return name; }
    public int getManaCost() { return manaCost; }
    public Rollable getRequiredDice() { return requiredDice; }
    public CardEffect getEffect() { return effect; }
}