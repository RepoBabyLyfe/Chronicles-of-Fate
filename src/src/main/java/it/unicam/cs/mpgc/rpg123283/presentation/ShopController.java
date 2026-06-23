package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.application.GameService;
import it.unicam.cs.mpgc.rpg123283.domain.Card;
import it.unicam.cs.mpgc.rpg123283.domain.CardCatalog;
import it.unicam.cs.mpgc.rpg123283.domain.PlayerProfile;
import it.unicam.cs.mpgc.rpg123283.infrastructure.SceneManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller del Negozio.
 * Mostra tutte le carte con badge prezzo, stato posseduta, e conferma acquisto.
 */
public class ShopController {

    @FXML private StackPane rootPane;
    @FXML private Canvas spaceCanvas;
    @FXML private Label crystalCountLabel;
    @FXML private FlowPane shopContainer;
    @FXML private Label statusLabel;
    @FXML private StackPane zoomOverlayPane;

    private SpaceBackgroundEngine spaceBackgroundEngine;
    private CardZoomOverlay cardZoomOverlay;

    private CardCatalog catalog;
    private PlayerProfile profile;

    @FXML
    public void initialize() {
        if (spaceCanvas != null && rootPane != null) {
            this.spaceBackgroundEngine = SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }

        GameService gameService = SceneManager.getInstance().getGameService();
        this.catalog = SceneManager.getInstance().getCardCatalog();
        this.profile = gameService.getPlayerProfile();

        //zoom overlay
        if (zoomOverlayPane != null) {
            this.cardZoomOverlay = new CardZoomOverlay(zoomOverlayPane);
        }

        updateView();
    }

    private void onCardClicked(Card card) {
        //se già posseduta, non fare nulla
        if (profile.hasCard(card.getName())) {
            statusLabel.setText("Già possiedi: " + card.getName());
            statusLabel.getStyleClass().removeAll("shop-status-success", "shop-status-error");
            return;
        }

        boolean success = SceneManager.getInstance().getGameService().buyCard(card);

        if (success) {
            statusLabel.setText("Acquisto completato! Sbloccata: " + card.getName());
            statusLabel.getStyleClass().removeAll("shop-status-error");
            statusLabel.getStyleClass().add("shop-status-success");

            //salvo il profilo non il combattimento
            SceneManager.getInstance().getGameService().saveProfile();
            updateView();
        } else {
            statusLabel.setText("Frammenti insufficienti per: " + card.getName()
                    + " (Costo: " + card.getPrice() + " ✨)");
            statusLabel.getStyleClass().removeAll("shop-status-success");
            statusLabel.getStyleClass().add("shop-status-error");
        }
    }

    private void updateView() {
        if (crystalCountLabel != null) {
            crystalCountLabel.setText(String.valueOf(profile.getEtherFragments()));
        }

        //pulisco e ricostruisco manualmente con badge prezzo
        shopContainer.getChildren().clear();

        List<Card> allCards = catalog.getAllCards().stream()
                .filter(c -> c.getPrice() > 0)
                .collect(java.util.stream.Collectors.toList());
        for (Card card : allCards) {
            boolean owned = profile.hasCard(card.getName());
            boolean canAfford = profile.getEtherFragments() >= card.getPrice();
            StackPane cardWithBadge = buildShopCard(card, owned, canAfford);
            shopContainer.getChildren().add(cardWithBadge);
        }
    }

    /**
     * Costruisce un nodo carta per il negozio con badge prezzo/stato sovrapposto
     */
    private StackPane buildShopCard(Card card, boolean owned, boolean canAfford) {
        StackPane wrapper = new StackPane();
        wrapper.setAlignment(Pos.TOP_CENTER);

        //usa un mini handviewrenderer per la singola carta
        FlowPane singleCardPane = new FlowPane();
        singleCardPane.setAlignment(Pos.CENTER);
        HandViewRenderer singleRenderer = new HandViewRenderer(singleCardPane, 0.75, 0, this::onCardClicked);
        singleRenderer.setHoverCallbacks(
                c -> { if (cardZoomOverlay != null) cardZoomOverlay.show(c); },
                c -> { if (cardZoomOverlay != null) cardZoomOverlay.hide(); }
        );

        if (owned) {
            singleRenderer.renderHand(List.of(card), 99, false,
                    List.of(card), java.util.Collections.emptyList());
        } else if (!canAfford) {
            singleRenderer.renderHand(List.of(card), 99, false,
                    List.of(card), java.util.Collections.emptyList());
        } else {
            singleRenderer.renderHand(List.of(card), 99, false);
        }

        //badge sovrapposto
        Label badge;
        if (owned) {
            badge = new Label("✔ POSSEDUTA");
            badge.getStyleClass().add("shop-owned-badge");
        } else {
            badge = new Label(card.getPrice() + " ✨");
            badge.getStyleClass().add("shop-price-badge");
            if (!canAfford) {
                badge.getStyleClass().add("shop-locked-price");
            }
        }

        VBox badgeContainer = new VBox(badge);
        badgeContainer.setAlignment(Pos.BOTTOM_CENTER);
        badgeContainer.setMouseTransparent(true);
        badgeContainer.setTranslateY(-10);

        wrapper.getChildren().addAll(singleCardPane, badgeContainer);
        return wrapper;
    }

    @FXML
    public void onBackClicked() {
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }
}