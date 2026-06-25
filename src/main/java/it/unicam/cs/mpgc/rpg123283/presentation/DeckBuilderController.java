package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.application.GameFactory;
import it.unicam.cs.mpgc.rpg123283.application.GameService;
import it.unicam.cs.mpgc.rpg123283.domain.Card;
import it.unicam.cs.mpgc.rpg123283.domain.CardCatalog;
import it.unicam.cs.mpgc.rpg123283.domain.Character;
import it.unicam.cs.mpgc.rpg123283.domain.DeckBuilder;
import it.unicam.cs.mpgc.rpg123283.infrastructure.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller del Deck Builder
 * Click diretto = seleziona/deseleziona carta. Hover = zoom preview laterale
 */
public class DeckBuilderController {

    @FXML private StackPane rootPane;
    @FXML private Canvas spaceCanvas;
    @FXML private FlowPane catalogContainer;
    @FXML private FlowPane deckContainer;
    @FXML private Label deckCountLabel;
    @FXML private Label focusLabel;
    @FXML private Button confirmButton;
    @FXML private StackPane zoomOverlayPane;
    @FXML private Label crystalCountLabel;

    private SpaceBackgroundEngine spaceBackgroundEngine;
    private CardCatalog catalog;
    private final DeckBuilder deckBuilder = new DeckBuilder();
    private it.unicam.cs.mpgc.rpg123283.domain.PlayerProfile profile;
    private HandViewRenderer catalogRenderer;
    private HandViewRenderer deckRenderer;
    private CardZoomOverlay cardZoomOverlay;

    @FXML
    public void initialize() {
        this.catalog = SceneManager.getInstance().getCardCatalog();
        this.profile = SceneManager.getInstance().getGameService().getPlayerProfile();

        if (spaceCanvas != null && rootPane != null) {
            this.spaceBackgroundEngine = SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }

        catalogRenderer = new HandViewRenderer(catalogContainer, 0.8, 0, this::onCatalogCardClicked);
        catalogRenderer.setHoverCallbacks(this::onCardHoverEnter, this::onCardHoverExit);

        deckRenderer = new HandViewRenderer(deckContainer, 0.65, 5, this::onDeckCardClicked);
        deckRenderer.setHoverCallbacks(this::onCardHoverEnter, this::onCardHoverExit);

        if (zoomOverlayPane != null) {
            this.cardZoomOverlay = new CardZoomOverlay(zoomOverlayPane);
        }

        List<Card> currentRecipe = SceneManager.getInstance().getGameService().getCustomDeckRecipe();
        for (Card c : currentRecipe) {
            deckBuilder.addCard(c);
        }

        updateView();
    }

    private void onCatalogCardClicked(Card card) {
        if (deckBuilder.getSelectedCards().contains(card)) {
            deckBuilder.removeCard(card);
        } else if (!deckBuilder.isComplete()) {
            deckBuilder.addCard(card);
        }
        updateView();
    }

    private void onDeckCardClicked(Card card) {
        deckBuilder.removeCard(card);
        updateView();
    }

    private void onCardHoverEnter(Card card) {
        if (cardZoomOverlay != null) {
            cardZoomOverlay.show(card);
        }
    }

    private void onCardHoverExit(Card card) {
        if (cardZoomOverlay != null) {
            cardZoomOverlay.hide();
        }
    }

    private void updateView() {
        List<Card> allCards = catalog.getAllCards();
        List<Card> notOwnedCards = allCards.stream()
                .filter(c -> !profile.hasCard(c.getName()))
                .collect(Collectors.toList());

        catalogRenderer.renderHand(allCards, 99, false,
                notOwnedCards, deckBuilder.getSelectedCards(), notOwnedCards);
        deckRenderer.renderHand(deckBuilder.getSelectedCards(), 99, false);

        deckCountLabel.setText("IL TUO MAZZO (" + deckBuilder.getSelectedCards().size() + "/5)");
        focusLabel.setText("Costo Medio Focus: " + deckBuilder.getAverageFocusCost());
        confirmButton.setDisable(!deckBuilder.isComplete());

        if (crystalCountLabel != null) {
            crystalCountLabel.setText(String.valueOf(profile.getEtherFragments()));
        }
    }

    @FXML
    public void onConfirmClicked() {
        GameService service = SceneManager.getInstance().getGameService();
        service.setCustomDeckRecipe(deckBuilder.getSelectedCards());
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }

    @FXML
    public void onBackClicked() {
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }
}