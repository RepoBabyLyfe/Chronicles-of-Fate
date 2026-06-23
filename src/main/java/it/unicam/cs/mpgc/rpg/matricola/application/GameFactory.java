package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.domain.EnemyArchetype;

/**
 * Factory dedicata alla generazione delle istanze dei personaggi.
 * Supporta la creazione di nemici parametrici basati su archetipi.
 */
public class GameFactory {

    public static Character createPlayer() {
        return new Character("Tessitore di Fati", 30, 10) {};
    }

    /**
     * Crea un nemico basato sull'archetipo specificato.
     */
    public static Character createEnemy(EnemyArchetype archetype) {
        return new Character(archetype.getBossName(), archetype.getBaseHp(), 0) {};
    }

    /**
     * Crea il nemico di default (Avatar dell'Entropia) — retrocompatibilità.
     */
    public static Character createEnemy() {
        return createEnemy(EnemyArchetype.ENTROPY_AVATAR);
    }
}