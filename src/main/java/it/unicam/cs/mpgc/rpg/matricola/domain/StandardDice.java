package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.random.RandomGenerator;

/**
 * Implementazione di un dado standard con un numero configurabile di facce
 * Utilizza la Dependency Injection per il generatore di numeri casuali
 */
public class StandardDice implements Rollable {

    private final int facce;
    private final RandomGenerator random;

    /**
     * Costruisce un dado standard.
     * * @param facce Numero di facce del dado (minimo 2)
     * @param random Generatore di numeri casuali iniettato dall'esterno
     */
    public StandardDice(int facce, RandomGenerator random) {
        if (facce < 2) {
            throw new IllegalArgumentException("Un dado standard deve avere almeno 2 facce.");
        }
        this.facce = facce;
        this.random = random;
    }

    @Override
    public RollResult roll() {
        int result = random.nextInt(facce) + 1;
        return new RollResult(result);
    }

    public int getFacce() {
        return facce;
    }
}