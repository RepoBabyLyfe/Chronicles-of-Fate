package it.unicam.cs.mpgc.rpg123283.domain;

//interfaccia per la logica AI del nemico
//responsabile esclusivamente della scelta della mossa del boss.
public interface IEnemyAI {
    BossMove takeTurn(Character boss, Character player);
}
