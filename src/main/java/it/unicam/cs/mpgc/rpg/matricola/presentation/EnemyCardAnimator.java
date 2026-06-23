package it.unicam.cs.mpgc.rpg.matricola.presentation;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class EnemyCardAnimator {

    private final VBox overlayContainer;
    private final ImageView cardImageView;
    private final Label fallbackText;

    public EnemyCardAnimator(VBox overlayContainer, ImageView cardImageView, Label fallbackText) {
        this.overlayContainer = overlayContainer;
        this.cardImageView = cardImageView;
        this.fallbackText = fallbackText;
    }

    public void playAnimation(String title, String imagePath, Runnable onAnimationFinished) {
        overlayContainer.setVisible(true);
        overlayContainer.setOpacity(0);
        overlayContainer.setTranslateY(-150); // Parte dall'alto (area del boss)

        // Setup Immagine o Fallback testo
        boolean imageLoaded = false;
        if (imagePath != null) {
            try {
                var stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    cardImageView.setImage(new Image(stream));
                    imageLoaded = true;
                }
            } catch (Exception ignored) {}
        }

        fallbackText.setText(imageLoaded ? "" : title);
        if (!imageLoaded) cardImageView.setImage(null);

        // Coreografia: Appare -> Scende -> Pausa drammatica -> Svanisce
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlayContainer);
        fadeIn.setToValue(1.0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(400), overlayContainer);
        slideDown.setToY(0);

        PauseTransition suspense = new PauseTransition(Duration.seconds(1.8));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), overlayContainer);
        fadeOut.setToValue(0.0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, slideDown, suspense, fadeOut);
        sequence.setOnFinished(e -> {
            overlayContainer.setVisible(false);
            onAnimationFinished.run(); // Segnala al Controller che ha finito
        });

        sequence.play();
    }
}