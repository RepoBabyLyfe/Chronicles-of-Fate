package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Definisce il contratto per qualsiasi entità che può subire effetti
 * (danni o cure) all'interno del gioco.
 */
public interface Targetable {

    /**
     * Applica un quantitativo di danno all'entità.
     * @param amount Quantità di danno (deve essere >= 0).
     */
    void takeDamage(int amount);

    /**
     * Ripristina i punti salute dell'entità.
     * @param amount Quantità di cure (deve essere >= 0).
     */
    void heal(int amount);

    /**
     * @return I punti salute attuali.
     */
    int getCurrentHp();

    /**
     * @return true se l'entità ha ancora punti salute, false altrimenti.
     */
    boolean isAlive();
}