package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.domain.Card;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * componente srp dedicato alla visualizzazione ingrandita di una carta
 * si posiziona come overlay centrato sullo schermo.
 * mouse-transparent per non intercettare i click sottostanti
 * utilizza un debounce sull'hide per evitare flickering durante il passaggio del mouse
 */
public class CardZoomOverlay {

    private static final Duration HIDE_DELAY = Duration.millis(200);
    private static final Duration FADE_DURATION = Duration.millis(150);

    private final StackPane overlayPane;
    private final ImageView zoomImage;
    private final VBox textFallbackLayer;
    private final Label costLabel;
    private final Label nameLabel;
    private final PauseTransition hideDebounce;
    private Card currentCard;

    public CardZoomOverlay(StackPane parentOverlay) {
        this.overlayPane = parentOverlay;
        this.overlayPane.setMouseTransparent(true);
        this.overlayPane.setVisible(false);
        this.overlayPane.setOpacity(0);

        //debounce per evitare flickering: l'hide viene ritardato
        //e cancellato se arriva un nuovo show() prima dello scadere
        this.hideDebounce = new PauseTransition(HIDE_DELAY);
        this.hideDebounce.setOnFinished(e -> fadeOut());

        //carta ingrandita
        StackPane cardFrame = new StackPane();
        cardFrame.getStyleClass().addAll("card-container", "zoom-card-frame");
        cardFrame.setMaxWidth(280);
        cardFrame.setMaxHeight(400);
        cardFrame.setScaleX(1.4);
        cardFrame.setScaleY(1.4);

        zoomImage = new ImageView();
        zoomImage.setFitWidth(280);
        zoomImage.setFitHeight(400);
        zoomImage.setPreserveRatio(false);

        Rectangle clip = new Rectangle(280, 400);
        clip.setArcWidth(32);
        clip.setArcHeight(32);
        zoomImage.setClip(clip);

        // fallback testuale
        textFallbackLayer = new VBox(15);
        textFallbackLayer.setAlignment(Pos.TOP_CENTER);
        textFallbackLayer.getStyleClass().add("card-text-padding");

        costLabel = new Label();
        costLabel.getStyleClass().add("card-cost");

        nameLabel = new Label();
        nameLabel.getStyleClass().add("card-title");

        textFallbackLayer.getChildren().addAll(costLabel, nameLabel);

        cardFrame.getChildren().addAll(zoomImage, textFallbackLayer);
        overlayPane.getChildren().add(cardFrame);
    }

    /**
     * mostra la carta ingrandita con transizione fade
     * cancella qualsiasi hide pendente (debounce)
     */
    public void show(Card card) {
        //cancella hide pendente - evita flickering
        hideDebounce.stop();

        if (card == currentCard && overlayPane.isVisible() && overlayPane.getOpacity() > 0.5) {
            return; //già visibile con la stessa carta
        }
        currentCard = card;

        //immagine specifica
        Image image = loadImage(card);
        if (image != null) {
            zoomImage.setImage(image);
            textFallbackLayer.setVisible(false);
        } else {
            zoomImage.setImage(null);
            textFallbackLayer.setVisible(true);
            costLabel.setText(card.getManaCost() + " Focus");
            nameLabel.setText(card.getName());
        }

        overlayPane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(FADE_DURATION, overlayPane);
        fadeIn.setFromValue(overlayPane.getOpacity());
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * richiede la chiusura dell'overlay con debounce
     * se show() viene chiamato entro HIDE_DELAY ms, l'hide viene annullato
     */
    public void hide() {
        hideDebounce.playFromStart();
    }

    private void fadeOut() {
        currentCard = null;
        FadeTransition fadeOutAnim = new FadeTransition(FADE_DURATION, overlayPane);
        fadeOutAnim.setFromValue(overlayPane.getOpacity());
        fadeOutAnim.setToValue(0);
        fadeOutAnim.setOnFinished(e -> overlayPane.setVisible(false));
        fadeOutAnim.play();
    }

    private Image loadImage(Card card) {
        if (card.getImagePath() == null) return null;
        return ImageCache.getImage(card.getImagePath());
    }
}
