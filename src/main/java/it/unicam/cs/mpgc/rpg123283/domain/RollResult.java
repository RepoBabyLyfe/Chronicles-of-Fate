package it.unicam.cs.mpgc.rpg123283.domain;

//rappresenta il risultato immutabile di un lancio di dado
//l'uso di un record previene side-effect indesiderati durante il calcolo dei danni
public record RollResult(int value) {
    public RollResult {
        if (value < 1) {
            throw new IllegalArgumentException("errore: il risultato di un dado non può essere minore di 1.");
        }
    }
}