package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Rappresenta la mossa scelta dall'Intelligenza Artificiale.
 * Include il tipo di carta per la differenziazione visiva nell'UI.
 */
public record BossMove(String name, String logMessage, String imagePath, EnemyCardType type) {

    /**
     * Costruttore retrocompatibile per usi senza tipo esplicito.
     */
    public BossMove(String name, String logMessage, String imagePath) {
        this(name, logMessage, imagePath, EnemyCardType.ATTACK);
    }
}