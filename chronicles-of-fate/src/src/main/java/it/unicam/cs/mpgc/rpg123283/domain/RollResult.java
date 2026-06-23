package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * Rappresenta il risultato immutabile di un lancio di dado.
 * L'uso di un Record previene side-effect indesiderati durante il calcolo dei danni.
 */
public record RollResult(int value) {
    public RollResult {
        if (value < 1) {
            throw new IllegalArgumentException("Errore: il risultato di un dado non può essere minore di 1.");
        }
    }
}