package it.unicam.cs.mpgc.rpg123283.presentation;

import javafx.scene.image.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache globale centralizzata per le immagini dell'interfaccia.
 * Previene multipli accessi al disco garantendo alte prestazioni
 * ed evitando lag spike durante l'animazione o l'hover delle carte.
 */
public class ImageCache {

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private ImageCache() {}

    /**
     * Recupera un'immagine dalla cache o, se non presente, la carica dal disco e la memorizza.
     * @param imagePath il percorso della risorsa (es. "/images/card.png")
     * @return l'oggetto Image caricato o null se non trovato
     */
    public static Image getImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        
        if (CACHE.containsKey(imagePath)) {
            return CACHE.get(imagePath);
        }

        try {
            var stream = ImageCache.class.getResourceAsStream(imagePath);
            if (stream != null) {
                Image img = new Image(stream);
                CACHE.put(imagePath, img);
                return img;
            }
        } catch (Exception ignored) {
            //se fallisce il caricamento, ritorna null in modo pulito
        }
        
        return null;
    }
}
