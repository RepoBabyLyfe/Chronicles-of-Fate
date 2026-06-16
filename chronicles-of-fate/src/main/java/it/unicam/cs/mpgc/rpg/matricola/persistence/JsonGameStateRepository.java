package it.unicam.cs.mpgc.rpg.matricola.persistence;

import it.unicam.cs.mpgc.rpg.matricola.application.GameState;
import it.unicam.cs.mpgc.rpg.matricola.application.GameStateRepository;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementazione concreta del salvataggio tramite file JSON minimale.
 */
public class JsonGameStateRepository implements GameStateRepository {

    private final Path filePath;

    public JsonGameStateRepository(String fileName) {
        this.filePath = Path.of(fileName);
    }

    @Override
    public void save(GameState state) throws Exception {
        // Costruiamo una stringa JSON manualmente in modo super safe
        String json = String.format(
                "{\n  \"playerHp\": %d,\n  \"playerFocus\": %d,\n  \"enemyHp\": %d\n}",
                state.playerHp(), state.playerFocus(), state.enemyHp()
        );
        Files.writeString(filePath, json);
        System.out.println("Partita salvata con successo in: " + filePath.toAbsolutePath());
    }

    @Override
    public GameState load() throws Exception {
        if (!saveExists()) {
            throw new IllegalStateException("Nessun salvataggio trovato.");
        }

        String json = Files.readString(filePath);

        // Estrazione manuale dei valori (Zero dipendenze esterne richieste)
        int playerHp = extractInt(json, "playerHp");
        int playerFocus = extractInt(json, "playerFocus");
        int enemyHp = extractInt(json, "enemyHp");

        System.out.println("Partita caricata con successo.");
        return new GameState(playerHp, playerFocus, enemyHp);
    }

    @Override
    public boolean saveExists() {
        return Files.exists(filePath);
    }

    // Metodo helper per parsare il JSON grezzo
    private int extractInt(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) throw new IllegalArgumentException("Chiave non trovata: " + key);

        int startIndex = index + searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("}", startIndex); // Se è l'ultimo elemento

        return Integer.parseInt(json.substring(startIndex, endIndex).trim());
    }
}