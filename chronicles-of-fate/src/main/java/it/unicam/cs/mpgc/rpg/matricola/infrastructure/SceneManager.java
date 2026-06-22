package it.unicam.cs.mpgc.rpg.matricola.infrastructure;

import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.persistence.JsonGameStateRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private GameService gameService;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
        // Inizializziamo il Service iniettando il nuovo EventBus globale
        this.gameService = new GameService(
                new JsonGameStateRepository("savegame.json"),
                GameEventBus.getInstance()
        );
    }

    public GameService getGameService() {
        return gameService;
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 1000, 750));
            } else {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}