package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Rappresenta una carta utilizzabile da un nemico.
 * Incapsula nome, descrizione, percorso immagine, tipo e i dettagli dell'effetto.
 */
public record EnemyCard(
        String name,
        String description,
        String imagePath,
        EnemyCardType type
) {
    public EnemyCard {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Il nome della carta non può essere vuoto.");
        }
    }
}
