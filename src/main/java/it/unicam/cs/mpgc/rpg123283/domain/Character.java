package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * entità base che rappresenta un personaggio nel gioco (Eroe o Mostro)
 * incapsula in modo sicuro la gestione dei Punti Salute (HP) e del Focus (Mana)
 */
public class Character implements Targetable {

    private final String name;
    private final int maxHp;
    private int currentHp;

    private final int maxFocus;
    private int currentFocus;

    public Character(String name, int maxHp, int maxFocus) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto.");
        }
        if (maxHp <= 0 || maxFocus < 0) {
            throw new IllegalArgumentException("Valori di HP o Focus non validi.");
        }
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp; //spawn con vita al massimo
        this.maxFocus = maxFocus;
        this.currentFocus = maxFocus; //spawn con il focus al massimo
    }

    //implementazione di Targetable

    @Override
    public void takeDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Danno negativo non consentito.");
        //controllo che impedisce di far scendere la vita sotto zero
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    @Override
    public void heal(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Cura negativa non consentita.");
        //controllo che impedisce di far superare la vita il massimo consentito
        this.currentHp = Math.min(this.maxHp, this.currentHp + amount);
    }

    @Override
    public int getCurrentHp() {
        return this.currentHp;
    }

    @Override
    public boolean isAlive() {
        return this.currentHp > 0;
    }

    //gestione del focus(mana)

    public boolean consumeFocus(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Costo negativo non consentito.");
        if (this.currentFocus >= amount) {
            this.currentFocus -= amount;
            return true; //focus consumato con successo
        }
        return false; //focus insufficiente
    }

    public void restoreFocus(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Ripristino negativo non consentito.");
        this.currentFocus = Math.min(this.maxFocus, this.currentFocus + amount);
    }

    public int getCurrentFocus() {
        return this.currentFocus;
    }



    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }
}