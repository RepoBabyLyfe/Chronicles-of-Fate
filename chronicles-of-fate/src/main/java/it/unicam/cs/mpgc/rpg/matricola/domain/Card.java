package it.unicam.cs.mpgc.rpg.matricola.domain;

public class Card {

    private final String name;
    private final int manaCost;
    private final Rollable requiredDice;
    private final CardEffect effect;
    private final String imagePath;

    public Card(String name, int manaCost, Rollable requiredDice, CardEffect effect, String imagePath) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della carta non può essere vuoto.");
        }
        if (manaCost < 0) {
            throw new IllegalArgumentException("Il costo in Focus non può essere negativo.");
        }
        if (effect == null) {
            throw new IllegalArgumentException("L'effetto non può essere nullo.");
        }

        this.name = name;
        this.manaCost = manaCost;
        this.requiredDice = requiredDice; //posso passare passare null
        this.effect = effect;
        this.imagePath = imagePath;
    }

    public Card(String name, int manaCost, Rollable requiredDice, CardEffect effect) {
        this(name, manaCost, requiredDice, effect, null);
    }

    // Metodo helper per controllare se la carta necessita di lanciare un dado
    public boolean requiresDice() {
        return this.requiredDice != null;
    }

    public String getName() { return name; }
    public int getManaCost() { return manaCost; }
    public Rollable getRequiredDice() { return requiredDice; }
    public CardEffect getEffect() { return effect; }
    public String getImagePath() { return imagePath; }
}