package it.unicam.cs.mpgc.rpg.matricola.domain;

public record DamageTier(int minRoll, int maxRoll, int damageAmount) {
    public DamageTier {
        if (minRoll > maxRoll) {
            throw new IllegalArgumentException("Il valore minimo non può superare il massimo");
        }
        if (damageAmount < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo");
        }
    }

    /**
     * Verifica se il risultato del dado rientra in questa fascia
     */
    public boolean contains(int rollValue) {
        return rollValue >= minRoll && rollValue <= maxRoll;
    }
}
