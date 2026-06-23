package it.unicam.cs.mpgc.rpg123283.application;

import it.unicam.cs.mpgc.rpg123283.domain.Character;
import it.unicam.cs.mpgc.rpg123283.domain.EnemyArchetype;

//factory dedicata alla generazione delle istanze dei personaggi
//supporta la creazione di nemici parametrici basati su archetipi
public class GameFactory {

    public static Character createPlayer() {
        return new Character("Tessitore di Fati", 45, 15) {};
    }

    //ritorna l'archetipo del boss in base al progresso (0 = Entropia, 1 = Leviatano, 2 = Assassino).
    //se maggiore di 2, ritorna l'ultimo (Assassino) come fallback.
    public static EnemyArchetype getEnemyArchetype(int defeatedBosses) {
        return switch (defeatedBosses) {
            case 0 -> EnemyArchetype.ENTROPY_AVATAR;
            case 1 -> EnemyArchetype.COSMIC_LEVIATHAN;
            default -> EnemyArchetype.NEBULA_ASSASSIN;
        };
    }

    //crea il nemico in base al progresso del giocatore
    public static Character createEnemy(int defeatedBosses) {
        EnemyArchetype archetype = getEnemyArchetype(defeatedBosses);
        return new Character(archetype.getBossName(), archetype.getBaseHp(), 0) {};
    }

    //crea il primo boss
    public static Character createEnemy() {
        return createEnemy(0);
    }
}