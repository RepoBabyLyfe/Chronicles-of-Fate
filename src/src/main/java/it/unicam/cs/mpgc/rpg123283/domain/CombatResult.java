package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * Risultato immutabile di un combattimento concluso.
 * Incapsula l'esito e le ricompense ottenute.
 */
public record CombatResult(CombatOutcome outcome, int fragmentsEarned) {

    private static final int VICTORY_FRAGMENTS = 50;

    public CombatResult {
        if (fragmentsEarned < 0) {
            throw new IllegalArgumentException("I frammenti guadagnati non possono essere negativi.");
        }
    }

    public static CombatResult victory() {
        return new CombatResult(CombatOutcome.VICTORY, VICTORY_FRAGMENTS);
    }

    public static CombatResult defeat() {
        return new CombatResult(CombatOutcome.DEFEAT, 0);
    }

    public boolean isVictory() {
        return outcome == CombatOutcome.VICTORY;
    }
}
