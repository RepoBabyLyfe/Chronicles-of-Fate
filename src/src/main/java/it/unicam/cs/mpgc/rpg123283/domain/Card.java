package it.unicam.cs.mpgc.rpg123283.domain;

public class Card {

    private final String name;
    private final int manaCost;
    private final Rollable requiredDice;
    private final CardEffect effect;
    private final String imagePath;
    private final int price;

    private Card(Builder builder) {
        if (builder.name == null || builder.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della carta non può essere vuoto.");
        }
        if (builder.manaCost < 0 || builder.price < 0) {
            throw new IllegalArgumentException("Costo in Focus o Prezzo non possono essere negativi.");
        }
        if (builder.effect == null) {
            throw new IllegalArgumentException("L'effetto non può essere nullo.");
        }

        this.name = builder.name;
        this.manaCost = builder.manaCost;
        this.requiredDice = builder.requiredDice;
        this.effect = builder.effect;
        this.imagePath = builder.imagePath;
        this.price = builder.price;
    }

    public static class Builder {
        private String name;
        private int manaCost;
        private Rollable requiredDice;
        private CardEffect effect;
        private String imagePath = null;
        private int price = 0;

        public Builder(String name) {
            this.name = name;
        }

        public Builder manaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        public Builder requiredDice(Rollable requiredDice) {
            this.requiredDice = requiredDice;
            return this;
        }

        public Builder effect(CardEffect effect) {
            this.effect = effect;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }

    public boolean requiresDice() { return this.requiredDice != null; }

    public String getName() { return name; }
    public int getManaCost() { return manaCost; }
    public Rollable getRequiredDice() { return requiredDice; }
    public CardEffect getEffect() { return effect; }
    public String getImagePath() { return imagePath; }
    public int getPrice() { return price; }
}