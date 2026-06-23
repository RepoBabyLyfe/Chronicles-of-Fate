package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * Interfaccia per la logica AI del nemico.
 * Responsabile esclusivamente della scelta della mossa del boss.
 */
public interface IEnemyAI {
    BossMove takeTurn(Character boss, Character player);
}
