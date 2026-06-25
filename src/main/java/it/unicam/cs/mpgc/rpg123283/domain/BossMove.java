package it.unicam.cs.mpgc.rpg123283.domain;

/**
rappresenta la mossa scelta dall'IA
include il tipo di carta per la differenziazione visiva nell'UI
*/
public record BossMove(String name, String logMessage, String imagePath, EnemyCardType type) {

    //costruttore retrocompatibile per usi senza tipo esplicito
    public BossMove(String name, String logMessage, String imagePath) {
        this(name, logMessage, imagePath, EnemyCardType.ATTACK);
    }
}