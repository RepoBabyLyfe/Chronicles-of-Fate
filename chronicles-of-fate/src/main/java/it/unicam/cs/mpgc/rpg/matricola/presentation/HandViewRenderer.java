package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.domain.Card;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

public class HandViewRenderer {

    private final HBox handContainer;
    private final Consumer<Card> onCardClicked;
    private Image cardTemplateImage;

    public HandViewRenderer(HBox handContainer, Consumer<Card> onCardClicked) {
        this.handContainer = handContainer;
        this.onCardClicked = onCardClicked;

        try {
            var imageStream = getClass().getResourceAsStream("/images/card_template.png");
            if (imageStream != null) {
                this.cardTemplateImage = new Image(imageStream);
            }
        } catch (Exception e) {
            System.out.println("[GUI WARN] Template grafico carta non trovato. Uso fallback CSS.");
        }
    }

    public void renderHand(List<Card> cards, int currentFocus, boolean isAnimating) {
        handContainer.getChildren().clear();
        if (isAnimating) return;

        for (Card card : cards) {
            // 1. LA RADICE
            StackPane cardRoot = new StackPane();
            cardRoot.getStyleClass().add("card-container");
            // Dimensioni BILANCIATE (280x400)
            cardRoot.setPrefWidth(280);
            cardRoot.setPrefHeight(400);

            // 2. LIVELLO INFERIORE: L'immagine
            ImageView bgImageView = new ImageView();
            Image specificImage = null;

            if (card.getImagePath() != null) {
                try {
                    var stream = getClass().getResourceAsStream(card.getImagePath());
                    if (stream != null) specificImage = new Image(stream);
                } catch (Exception e) {
                    System.out.println("Immagine non trovata: " + card.getImagePath());
                }
            }

            if (specificImage != null) {
                bgImageView.setImage(specificImage);
            } else if (cardTemplateImage != null) {
                bgImageView.setImage(cardTemplateImage);
            }

            if (bgImageView.getImage() != null) {
                // Adattiamo l'immagine alle nuove dimensioni
                bgImageView.setFitWidth(280);
                bgImageView.setFitHeight(400);
                bgImageView.setPreserveRatio(false);

                // Maschera di ritaglio arrotondata adattata
                Rectangle clip = new Rectangle(280, 400);
                clip.setArcWidth(32);
                clip.setArcHeight(32);
                bgImageView.setClip(clip);
            }

            // Aggiungiamo SEMPRE lo sfondo
            cardRoot.getChildren().add(bgImageView);

            // 3. LIVELLO SUPERIORE (TESTI): Solo per carte generiche
            if (specificImage == null) {
                VBox textLayer = new VBox(15);
                textLayer.setAlignment(Pos.TOP_CENTER);
                // Padding riadattato
                textLayer.setStyle("-fx-padding: 25px 10px 10px 10px;");

                Label costLabel = new Label(card.getManaCost() + " Focus");
                costLabel.getStyleClass().add("card-cost");

                Label titleLabel = new Label(card.getName());
                titleLabel.getStyleClass().add("card-title");

                textLayer.getChildren().addAll(costLabel, titleLabel);
                cardRoot.getChildren().add(textLayer);
            }

            // 4. Logica di disattivazione o click
            if (card.getManaCost() > currentFocus) {
                cardRoot.setOpacity(0.4);
            } else {
                cardRoot.setOnMouseClicked(e -> onCardClicked.accept(card));
            }

            handContainer.getChildren().add(cardRoot);
        }
    }
}