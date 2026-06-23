package it.unicam.cs.mpgc.rpg.matricola.application;

import java.util.Set;

/**
 * Record immutabile che rappresenta lo stato della partita da salvare o caricare.
 * Il flag hasCombatData distingue salvataggi dal negozio (solo profilo) da quelli in combattimento.
 */
public record GameState(
        int playerHp,
        int playerFocus,
        int enemyHp,
        int etherFragments,
        Set<String> unlockedCards,
        boolean hasCombatData
) {
    public GameState {
        if (playerHp < 0 || playerFocus < 0 || enemyHp < 0) {
            throw new IllegalArgumentException("I valori di stato non possono essere negativi.");
        }
    }

    /**
     * Costruttore per salvataggi completi (durante il combattimento).
     */
    public static GameState withCombat(int playerHp, int playerFocus, int enemyHp,
                                       int etherFragments, Set<String> unlockedCards) {
        return new GameState(playerHp, playerFocus, enemyHp, etherFragments, unlockedCards, true);
    }

    /**
     * Costruttore per salvataggi solo-profilo (dal negozio o menu).
     */
    public static GameState profileOnly(int etherFragments, Set<String> unlockedCards) {
        return new GameState(0, 0, 0, etherFragments, unlockedCards, false);
    }
}