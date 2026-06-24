package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * strategy pattern: definisce il contratto per l'effetto di una carta
 * permette di aggiungere nuovi effetti (es. Cura, Ricarica Focus) manipolando sia il bersaglio che la sorgente
 */
@FunctionalInterface
public interface CardEffect {

    /**
     * Applica l'effetto della carta.
     * @param source Chi ha giocato la carta (es. l'Eroe).
     * @param target L'entità che subisce l'effetto (es. il Boss).
     * @param roll Il risultato immutabile del dado.
     */
    void apply(Character source, Targetable target, RollResult roll);
}