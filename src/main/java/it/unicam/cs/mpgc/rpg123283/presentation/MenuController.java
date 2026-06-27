package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.application.GameFactory;
import it.unicam.cs.mpgc.rpg123283.application.GameService;
import it.unicam.cs.mpgc.rpg123283.domain.Character;
import it.unicam.cs.mpgc.rpg123283.infrastructure.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

public class MenuController {
    @FXML private StackPane rootPane;
    @FXML private Canvas spaceCanvas;
    @FXML private javafx.scene.control.Label crystalCountLabel;


    @FXML
    public void initialize() {
        if (spaceCanvas != null && rootPane != null) {
            SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }
        
        GameService service = SceneManager.getInstance().getGameService();
        if (service != null && service.getPlayerProfile() != null && crystalCountLabel != null) {
            crystalCountLabel.setText(String.valueOf(service.getPlayerProfile().getEtherFragments()));
        }
    }

    @FXML
    public void onCompendiumClicked() {
        SceneManager.getInstance().switchScene("/deck_builder_view.fxml");
    }

    @FXML
    public void onStartGameClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler procedere? Perderai tutti i progressi fatti fin'ora.", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            GameService service = SceneManager.getInstance().getGameService();
            service.wipeProfile();
            
            Character player = GameFactory.createPlayer();
            Character enemy = GameFactory.createEnemy(0);
            service.startNewGame(player, enemy);
            SceneManager.getInstance().switchScene("/combat_view.fxml");
        }
    }

    @FXML
    public void onContinueClicked() {
        GameService service = SceneManager.getInstance().getGameService();
        Character player = GameFactory.createPlayer();

        try {
            service.loadGame(player);
            
            if (service.getCombatManager() == null) {
                int defeatedBosses = service.getPlayerProfile().getDefeatedBosses();
                Character enemy = GameFactory.createEnemy(defeatedBosses);
                service.startNewGame(player, enemy);
            }
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