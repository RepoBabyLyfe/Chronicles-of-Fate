package it.unicam.cs.mpgc.rpg.matricola.application;

/**
 * Record immutabile che rappresenta lo stato della partita da salvare o caricare.
 */
public record GameState(
        int playerHp,
        int playerFocus,
        int enemyHp
) {
    public GameState {
        if (playerHp < 0 || playerFocus < 0 || enemyHp < 0) {
            throw new IllegalArgumentException("I valori di stato non possono essere negativi.");
        }
    }
}