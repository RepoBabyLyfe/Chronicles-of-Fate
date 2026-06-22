package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.application.GameFactory;
import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.infrastructure.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

public class MenuController {
    @FXML private StackPane rootPane;
    @FXML private Canvas spaceCanvas;
    private SpaceBackgroundEngine spaceBackgroundEngine;

    @FXML
    public void initialize() {
        if (spaceCanvas != null && rootPane != null) {
            rootPane.widthProperty().addListener((obs, oldVal, newVal) -> spaceCanvas.setWidth(newVal.doubleValue()));
            rootPane.heightProperty().addListener((obs, oldVal, newVal) -> spaceCanvas.setHeight(newVal.doubleValue()));
            spaceCanvas.setWidth(rootPane.getPrefWidth());
            spaceCanvas.setHeight(rootPane.getPrefHeight());

            this.spaceBackgroundEngine = new SpaceBackgroundEngine(spaceCanvas);
            rootPane.setOnMouseMoved(e -> spaceBackgroundEngine.updateMouseCoordinates(e.getX(), e.getY()));
            this.spaceBackgroundEngine.start();
        }
    }

    @FXML
    public void onStartGameClicked() {
        SceneManager.getInstance().switchScene("/deck_builder_view.fxml");
    }

    @FXML
    public void onLoadGameClicked() {
        GameService service = SceneManager.getInstance().getGameService();
        Character player = GameFactory.createPlayer();
        Character enemy = GameFactory.createEnemy();

        try {
            service.loadGame(player, enemy);
            SceneManager.getInstance().switchScene("/combat_view.fxml");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nessun salvataggio trovato o file corrotto.");
            alert.showAndWait();
        }
    }

    @FXML
    public void onExitClicked() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void onShopClicked() {
        SceneManager.getInstance().switchScene("/shop_view.fxml");
    }
}