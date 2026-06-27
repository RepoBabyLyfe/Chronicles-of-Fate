package it.unicam.cs.mpgc.rpg123283.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.unicam.cs.mpgc.rpg123283.domain.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;

//funge da archivio per tutte le carte disponibili nel gioco leggendole da JSON.

public class JsonCardCatalog implements CardCatalog {

    private static final Logger LOGGER = Logger.getLogger(JsonCardCatalog.class.getName());
    private final List<Card> availableCards = new ArrayList<>();

    //DTO interno per la deserializzazione
    private static class CardDTO {
        String name;
        int manaCost;
        boolean requiresDice;
        String effectType;
        int baseValue;
        int diceMultiplier;
        int selfDamage;
        String imagePath;
        int price;
    }

    public JsonCardCatalog() {
        loadFromJson();
    }

    private void loadFromJson() {
        Gson gson = new Gson();
        java.io.InputStream is = getClass().getResourceAsStream("/data/cards.json");
        if (is == null) {
            LOGGER.severe("Errore critico: File /data/cards.json non trovato nelle risorse!");
            return;
        }

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            
            Type listType = new TypeToken<ArrayList<CardDTO>>(){}.getType();
            List<CardDTO> dtos = gson.fromJson(reader, listType);

            Rollable d6 = new StandardDice(6, RandomGenerator.getDefault());

            for (CardDTO dto : dtos) {
                EffectType type = EffectType.valueOf(dto.effectType);
                
                CardEffect effect = (source, target, roll) -> {
                    int diceValue = roll != null ? roll.value() : 0;
                    type.execute(source, target, diceValue, dto.baseValue, dto.diceMultiplier, dto.selfDamage);
                };

                Card.Builder builder = new Card.Builder(dto.name)
                        .manaCost(dto.manaCost)
                        .effect(effect)
                        .imagePath(dto.imagePath)
                        .price(dto.price);
                
                if (dto.requiresDice) {
                    builder.requiredDice(d6);
                }

                availableCards.add(builder.build());
            }
            LOGGER.info("Caricate " + availableCards.size() + " carte dal file JSON in modo dinamico.");
        } catch (Exception e) {
            LOGGER.severe("Errore critico durante il caricamento di cards.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * @return Una copia non modificabile del catalogo completo.
     */
    @Override
    public List<Card> getAllCards() {
        return List.copyOf(availableCards);
    }
}
