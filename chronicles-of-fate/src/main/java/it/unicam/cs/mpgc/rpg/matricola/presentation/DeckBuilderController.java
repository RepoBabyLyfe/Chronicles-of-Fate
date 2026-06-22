package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.application.GameFactory;
import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.domain.Card;
import it.unicam.cs.mpgc.rpg.matricola.domain.CardCatalog;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.domain.DeckBuilder;
import it.unicam.cs.mpgc.rpg.matricola.infrastructure.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller del Deck Builder.
 * Click diretto = seleziona/deseleziona carta. Hover = zoom preview laterale.
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
    private final CardCatalog catalog = new CardCatalog();
    private final DeckBuilder deckBuilder = new DeckBuilder();
    private it.unicam.cs.mpgc.rpg.matricola.domain.PlayerProfile profile;
    private HandViewRenderer catalogRenderer;
    private HandViewRenderer deckRenderer;
    private CardZoomOverlay cardZoomOverlay;

    @FXML
    public void initialize() {
        this.profile = SceneManager.getInstance().getGameService().getPlayerProfile();

        if (spaceCanvas != null && rootPane != null) {
            this.spaceBackgroundEngine = SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }

        // Catalogo: click diretto per aggiungere al mazzo
        catalogRenderer = new HandViewRenderer(catalogContainer, 0.8, 0, this::onCatalogCardClicked);
        catalogRenderer.setHoverCallbacks(this::onCardHoverEnter, this::onCardHoverExit);

        // Mazzo: click per rimuovere, 5 slot fissi
        deckRenderer = new HandViewRenderer(deckContainer, 0.65, 5, this::onDeckCardClicked);
        deckRenderer.setHoverCallbacks(this::onCardHoverEnter, this::onCardHoverExit);

        // Zoom overlay laterale
        if (zoomOverlayPane != null) {
            this.cardZoomOverlay = new CardZoomOverlay(zoomOverlayPane);
        }

        updateView();
    }

    // --- Click diretto: toggle selezione ---

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

    // --- Hover: zoom preview ---

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

    // --- Aggiornamento Vista ---

    private void updateView() {
        List<Card> ownedCards = catalog.getAllCards().stream()
                .filter(c -> profile.hasCard(c.getName()))
                .collect(Collectors.toList());

        // Le carte già nel mazzo vengono evidenziate (selectedCards), non disabilitate
        catalogRenderer.renderHand(ownedCards, 99, false,
                java.util.Collections.emptyList(), deckBuilder.getSelectedCards());
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
        Character player = GameFactory.createPlayer();
        Character enemy = GameFactory.createEnemy();
        service.startNewGame(player, enemy);
        SceneManager.getInstance().switchScene("/combat_view.fxml");
    }

    @FXML
    public void onBackClicked() {
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }
}