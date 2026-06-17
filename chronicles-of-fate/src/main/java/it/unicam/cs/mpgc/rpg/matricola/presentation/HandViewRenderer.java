package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.domain.Card;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.List;
import java.util.function.Consumer;

public class HandViewRenderer {

    private final HBox handContainer;
    private final Consumer<Card> onCardClicked;

    public HandViewRenderer(HBox handContainer, Consumer<Card> onCardClicked) {
        this.handContainer = handContainer;
        this.onCardClicked = onCardClicked;
    }

    public void renderHand(List<Card> cards, int currentFocus, boolean isAnimating) {
        handContainer.getChildren().clear();
        if (isAnimating) return;

        for (Card card : cards) {
            VBox cardLayout = new VBox();
            cardLayout.getStyleClass().add("card-container");
            cardLayout.setAlignment(Pos.CENTER);
            cardLayout.setPrefWidth(120);
            cardLayout.setPrefHeight(180);

            Label costLabel = new Label("Cost: " + card.getManaCost());
            costLabel.getStyleClass().add("card-cost");

            Label titleLabel = new Label(card.getName());
            titleLabel.getStyleClass().add("card-title");

            cardLayout.getChildren().addAll(costLabel, titleLabel);

            if (card.getManaCost() > currentFocus) {
                cardLayout.setOpacity(0.4);
            } else {
                cardLayout.setOnMouseClicked(e -> onCardClicked.accept(card));
            }
            handContainer.getChildren().add(cardLayout);
        }
    }
}