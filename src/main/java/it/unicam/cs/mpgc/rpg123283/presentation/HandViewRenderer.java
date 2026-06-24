package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.domain.Card;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class HandViewRenderer {

    private static final Logger LOGGER = Logger.getLogger(HandViewRenderer.class.getName());

    private final Pane handContainer;
    private final Consumer<Card> onCardClicked;
    private Consumer<Card> onCardHoverEnter;
    private Consumer<Card> onCardHoverExit;
    private Image cardTemplateImage;
    private final double scaleFactor;
    private final int maxSlots;

    public HandViewRenderer(Pane handContainer, Consumer<Card> onCardClicked) {
        this(handContainer, 1.0, 0, onCardClicked);
    }

    public HandViewRenderer(Pane handContainer, double scaleFactor, int maxSlots, Consumer<Card> onCardClicked) {
        this.handContainer = handContainer;
        this.scaleFactor = scaleFactor;
        this.maxSlots = maxSlots;
        this.onCardClicked = onCardClicked;

        try {
            this.cardTemplateImage = ImageCache.getImage("/images/card_template.png");
        } catch (Exception e) {
            LOGGER.warning("[GUI WARN] Template grafico carta non trovato.");
        }
    }

    public void setHoverCallbacks(Consumer<Card> onEnter, Consumer<Card> onExit) {
        this.onCardHoverEnter = onEnter;
        this.onCardHoverExit = onExit;
    }

    public void renderHand(List<Card> cards, int currentFocus, boolean isAnimating) {
        renderHand(cards, currentFocus, isAnimating, Collections.emptyList(), Collections.emptyList());
    }

    public void renderHand(List<Card> cards, int currentFocus, boolean isAnimating, List<Card> disabledCards) {
        renderHand(cards, currentFocus, isAnimating, disabledCards, Collections.emptyList(), Collections.emptyList());
    }

    public void renderHand(List<Card> cards, int currentFocus, boolean isAnimating,
                           List<Card> disabledCards, List<Card> selectedCards) {
        renderHand(cards, currentFocus, isAnimating, disabledCards, selectedCards, Collections.emptyList());
    }

    public void renderHand(List<Card> cards, int currentFocus, boolean isAnimating,
                           List<Card> disabledCards, List<Card> selectedCards, List<Card> mysteryCards) {
        handContainer.getChildren().clear();
        if (isAnimating) return;

        int drawnCards = 0;

        for (Card card : cards) {
            boolean isSelected = selectedCards.contains(card);
            boolean isMystery = mysteryCards.contains(card);
            StackPane cardRoot = buildCardNode(card, currentFocus, disabledCards, isSelected, isMystery);
            wrapAndAdd(cardRoot);
            drawnCards++;
        }

        while (drawnCards < maxSlots) {
            StackPane emptySlot = new StackPane();
            emptySlot.setPrefWidth(280);
            emptySlot.setPrefHeight(400);
            emptySlot.getStyleClass().add("empty-card-slot");

            Label emptyLabel = new Label("SLOT VUOTO");
            emptyLabel.getStyleClass().add("empty-slot-label");
            emptySlot.getChildren().add(emptyLabel);

            wrapAndAdd(emptySlot);
            drawnCards++;
        }
    }

    private void wrapAndAdd(StackPane cardRoot) {
        if (scaleFactor != 1.0) {
            cardRoot.setScaleX(scaleFactor);
            cardRoot.setScaleY(scaleFactor);
            handContainer.getChildren().add(new Group(cardRoot));
        } else {
            handContainer.getChildren().add(cardRoot);
        }
    }

    private StackPane buildCardNode(Card card, int currentFocus, List<Card> disabledCards, boolean isSelected, boolean isMystery) {
        StackPane cardRoot = new StackPane();
        cardRoot.getStyleClass().add("card-container");
        if (isSelected) {
            cardRoot.getStyleClass().add("card-selected");
        }
        cardRoot.setPrefWidth(280);
        cardRoot.setPrefHeight(400);

        if (isMystery) {
            ImageView bgImageView = new ImageView();
            if (cardTemplateImage != null) bgImageView.setImage(cardTemplateImage);
            bgImageView.setFitWidth(280);
            bgImageView.setFitHeight(400);
            
            Label mysteryLabel = new Label("?");
            mysteryLabel.setStyle("-fx-font-size: 80px; -fx-text-fill: white; -fx-font-family: 'Georgia';");
            
            Label unlockLabel = new Label("Sconfiggi il Boss");
            unlockLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa; -fx-translate-y: 60px;");
            
            cardRoot.getChildren().addAll(bgImageView, mysteryLabel, unlockLabel);
            cardRoot.setOpacity(0.5);
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setSaturation(-1.0);
            cardRoot.setEffect(grayscale);
            return cardRoot;
        }

        ImageView bgImageView = createCardImage(card);
        cardRoot.getChildren().add(bgImageView);

        Image specificImage = loadSpecificImage(card);
        if (specificImage == null) {
            VBox textLayer = createTextLayer(card);
            cardRoot.getChildren().add(textLayer);
        }

        if (card.getManaCost() > currentFocus || disabledCards.contains(card)) {
            cardRoot.setOpacity(0.5);
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setSaturation(-1.0);
            cardRoot.setEffect(grayscale);
        } else {
            cardRoot.setOnMouseClicked(e -> onCardClicked.accept(card));
        }

        if (onCardHoverEnter != null) {
            cardRoot.setOnMouseEntered(e -> onCardHoverEnter.accept(card));
        }
        if (onCardHoverExit != null) {
            cardRoot.setOnMouseExited(e -> onCardHoverExit.accept(card));
        }

        return cardRoot;
    }

    private ImageView createCardImage(Card card) {
        ImageView bgImageView = new ImageView();
        Image specificImage = loadSpecificImage(card);

        if (specificImage != null) bgImageView.setImage(specificImage);
        else if (cardTemplateImage != null) bgImageView.setImage(cardTemplateImage);

        if (bgImageView.getImage() != null) {
            bgImageView.setFitWidth(280);
            bgImageView.setFitHeight(400);
            bgImageView.setPreserveRatio(false);

            Rectangle clip = new Rectangle(280, 400);
            clip.setArcWidth(32);
            clip.setArcHeight(32);
            bgImageView.setClip(clip);
        }

        return bgImageView;
    }

    private Image loadSpecificImage(Card card) {
        if (card.getImagePath() == null) return null;
        return ImageCache.getImage(card.getImagePath());
    }

    private VBox createTextLayer(Card card) {
        VBox textLayer = new VBox(15);
        textLayer.setAlignment(Pos.TOP_CENTER);
        textLayer.getStyleClass().add("card-text-padding");

        Label costLabel = new Label(card.getManaCost() + " Focus");
        costLabel.getStyleClass().add("card-cost");

        Label titleLabel = new Label(card.getName());
        titleLabel.getStyleClass().add("card-title");

        textLayer.getChildren().addAll(costLabel, titleLabel);
        return textLayer;
    }
}