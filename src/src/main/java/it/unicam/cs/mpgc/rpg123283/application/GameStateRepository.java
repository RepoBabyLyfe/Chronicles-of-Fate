package it.unicam.cs.mpgc.rpg123283.application;

/**
 * Interfaccia per la gestione della persistenza.
 * Isola l'Application Layer dai dettagli tecnici del salvataggio.
 */
public interface GameStateRepository {

    void save(GameState state) throws Exception;

    GameState load() throws Exception;

    boolean saveExists();
}