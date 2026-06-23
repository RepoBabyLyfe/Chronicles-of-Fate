package it.unicam.cs.mpgc.rpg.matricola.domain;

public class Card {

    private final String name;
    private final int manaCost;
    private final Rollable requiredDice;
    private final CardEffect effect;
    private final String imagePath;
    private final int price; // NUOVO: Costo in Frammenti di Etere

    // Costruttore completo (Usato per le carte del negozio)
    public Card(String name, int manaCost, Rollable requiredDice, CardEffect effect, String imagePath, int price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della carta non può essere vuoto.");
        }
        if (manaCost < 0 || price < 0) {
            throw new IllegalArgumentException("Costo in Focus o Prezzo non possono essere negativi.");
        }
        if (effect == null) {
            throw new IllegalArgumentException("L'effetto non può essere nullo.");
        }

        this.name = name;
        this.manaCost = manaCost;
        this.requiredDice = requiredDice;
        this.effect = effect;
        this.imagePath = imagePath;
        this.price = price;
    }

    // Costruttore retrocompatibile (Le vecchie carte avranno prezzo 0 di default)
    public Card(String name, int manaCost, Rollable requiredDice, CardEffect effect, String imagePath) {
        this(name, manaCost, requiredDice, effect, imagePath, 0);
    }

    public Card(String name, int manaCost, Rollable requiredDice, CardEffect effect) {
        this(name, manaCost, requiredDice, effect, null, 0);
    }

    public boolean requiresDice() { return this.requiredDice != null; }

    public String getName() { return name; }
    public int getManaCost() { return manaCost; }
    public Rollable getRequiredDice() { return requiredDice; }
    public CardEffect getEffect() { return effect; }
    public String getImagePath() { return imagePath; }
    public int getPrice() { return price; } // NUOVO GETTER
}