package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * Rappresenta la mossa scelta dall'Intelligenza Artificiale.
 * Include il tipo di carta per la differenziazione visiva nell'UI.
 */
public record BossMove(String name, String logMessage, String imagePath, EnemyCardType type) {

    //costruttore retrocompatibile senza tipo esplicito
    public BossMove(String name, String logMessage, String imagePath) {
        this(name, logMessage, imagePath, EnemyCardType.ATTACK);
    }
}