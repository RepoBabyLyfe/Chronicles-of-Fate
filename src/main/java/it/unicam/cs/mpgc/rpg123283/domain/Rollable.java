package it.unicam.cs.mpgc.rpg123283.domain;

//interfaccia che definisce il contratto per qualsiasi generatore di risultati aleatori
public interface Rollable {
    /**
     * //esegue un lancio e restituisce un risultato garantito e immutabile
     * // @return RollResult l'esito del lancio
     */
    RollResult roll();
}
