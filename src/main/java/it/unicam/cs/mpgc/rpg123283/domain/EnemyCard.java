package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * Rappresenta una carta utilizzabile da un nemico
 * Incapsula nome, descrizione, percorso immagine, tipo e i parametri numerici dell'effetto.
 * I campi primaryValue e secondaryValue eliminano la necessità di switch per archetipo nel BossAI.
 *
 * @param primaryValue   Danno per ATTACK/SPECIAL, drain-damage per DRAIN, focus-loss per DEBUFF.
 * @param secondaryValue Heal per DRAIN, fallback-damage per DEBUFF (0 se non applicabile).
 */
public record EnemyCard(
        String name,
        String description,
        String imagePath,
        EnemyCardType type,
        int primaryValue,
        int secondaryValue
) {
    public EnemyCard {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome della carta non può essere vuoto.");
        }
    }
}
