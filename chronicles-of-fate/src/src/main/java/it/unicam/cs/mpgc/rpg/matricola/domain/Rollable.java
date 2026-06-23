package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Interfaccia che definisce il contratto per qualsiasi generatore di risultati aleatori.
 */
public interface Rollable {

    /**
     * Esegue un lancio e restituisce un risultato garantito e immutabile.,
     * * @return RollResult l'esito del lancio.
     */
    RollResult roll();
}
