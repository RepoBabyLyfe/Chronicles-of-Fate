package it.unicam.cs.mpgc.rpg123283.application;

/**
 * interfaccia per la gestione della persistenza
 * isola l'application layer dai dettagli tecnici del salvataggio
 */
public interface GameStateRepository {

    void save(GameState state) throws Exception;

    GameState load() throws Exception;

    boolean saveExists();
}