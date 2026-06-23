package it.unicam.cs.mpgc.rpg.matricola.persistence;

import it.unicam.cs.mpgc.rpg.matricola.application.GameState;
import it.unicam.cs.mpgc.rpg.matricola.application.GameStateRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Implementazione concreta del salvataggio tramite file JSON minimale e sicuro.
 * Supporta il flag hasCombatData per distinguere salvataggi dal combattimento vs profilo-only.
 */
public class JsonGameStateRepository implements GameStateRepository {

    private static final Logger LOGGER = Logger.getLogger(JsonGameStateRepository.class.getName());

    private final Path filePath;

    public JsonGameStateRepository(String fileName) {
        this.filePath = Path.of(fileName);
    }

    @Override
    public void save(GameState state) throws Exception {
        String cardsJson = state.unlockedCards().stream()
                .map(name -> "\"" + escapeJsonString(name) + "\"")
                .collect(Collectors.joining(", "));

        String json = String.format(
                "{\n  \"hasCombatData\": %b,\n  \"playerHp\": %d,\n  \"playerFocus\": %d,\n  \"enemyHp\": %d,\n  \"etherFragments\": %d,\n  \"unlockedCards\": [%s]\n}",
                state.hasCombatData(), state.playerHp(), state.playerFocus(), state.enemyHp(),
                state.etherFragments(), cardsJson
        );
        Files.writeString(filePath, json);
        LOGGER.info("Scrittura JSON completata in: " + filePath.toAbsolutePath());
    }

    @Override
    public GameState load() throws Exception {
        if (!saveExists()) {
            throw new IllegalStateException("Nessun salvataggio trovato.");
        }

        String json = Files.readString(filePath);

        boolean hasCombatData = extractBoolean(json, "hasCombatData");
        int playerHp = extractInt(json, "playerHp");
        int playerFocus = extractInt(json, "playerFocus");
        int enemyHp = extractInt(json, "enemyHp");
        int etherFragments = extractInt(json, "etherFragments");
        Set<String> unlockedCards = extractStringArray(json, "unlockedCards");

        return new GameState(playerHp, playerFocus, enemyHp, etherFragments, unlockedCards, hasCombatData);
    }

    @Override
    public boolean saveExists() {
        return Files.exists(filePath);
    }

    /**
     * Estrae un valore intero dal JSON cercando la chiave specificata.
     * Gestisce correttamente il caso in cui il valore preceda un array o un booleano.
     */
    private int extractInt(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) return 0; // Default sicuro per campi mancanti (retrocompatibilità)

        int startIndex = index + searchKey.length();
        // Trova la fine del valore: il prossimo separatore (virgola, chiusura oggetto, o inizio array)
        int endIndex = findValueEnd(json, startIndex);

        return Integer.parseInt(json.substring(startIndex, endIndex).trim());
    }

    /**
     * Estrae un valore booleano dal JSON.
     */
    private boolean extractBoolean(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) return false; // Retrocompatibilità con vecchi save senza questo campo

        int startIndex = index + searchKey.length();
        int endIndex = findValueEnd(json, startIndex);

        return Boolean.parseBoolean(json.substring(startIndex, endIndex).trim());
    }

    /**
     * Trova la fine di un valore scalare nel JSON (non stringa, non array).
     */
    private int findValueEnd(String json, int startIndex) {
        int commaIdx = json.indexOf(",", startIndex);
        int braceIdx = json.indexOf("}", startIndex);
        int bracketIdx = json.indexOf("\n", startIndex);

        int endIndex = json.length();
        if (commaIdx != -1 && commaIdx < endIndex) endIndex = commaIdx;
        if (braceIdx != -1 && braceIdx < endIndex) endIndex = braceIdx;
        if (bracketIdx != -1 && bracketIdx < endIndex) endIndex = bracketIdx;

        return endIndex;
    }

    /**
     * Estrae un array di stringhe dal JSON.
     */
    private Set<String> extractStringArray(String json, String key) {
        Set<String> result = new HashSet<>();
        String searchKey = "\"" + key + "\":";
        int index = json.indexOf(searchKey);
        if (index == -1) return result;

        int arrayStart = json.indexOf("[", index);
        int arrayEnd = json.indexOf("]", arrayStart);
        if (arrayStart == -1 || arrayEnd == -1) return result;

        String arrayContent = json.substring(arrayStart + 1, arrayEnd).trim();
        if (arrayContent.isEmpty()) return result;

        String[] items = arrayContent.split(",");
        for (String item : items) {
            String cleaned = item.trim().replace("\"", "");
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }
        return result;
    }

    /**
     * Escaping minimale per valori stringa JSON.
     */
    private String escapeJsonString(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}