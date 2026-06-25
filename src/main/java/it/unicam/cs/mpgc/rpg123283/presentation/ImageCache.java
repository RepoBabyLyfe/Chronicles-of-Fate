package it.unicam.cs.mpgc.rpg123283.presentation;

import javafx.scene.image.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageCache {

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private ImageCache() {}

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
        }
        
        return null;
    }
}
