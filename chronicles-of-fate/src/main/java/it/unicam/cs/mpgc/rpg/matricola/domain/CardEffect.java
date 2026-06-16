package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Strategy Pattern: Definisce il contratto per l'effetto di una carta.
 * Permette di aggiungere nuovi effetti (es. Veleno, Stun) senza modificare il core, rispettando l'Open-Closed Principle.
 */
public interface CardEffect {

    /**
     * Applica l'effetto sul bersaglio basandosi sul risultato del dado
     * @param target L'entità che subisce l'effetto.
     * @param roll Il risultato immutabile del dado.
     */
    void apply(Targetable target, RollResult roll);
}