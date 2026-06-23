package it.unicam.cs.mpgc.rpg123283.infrastructure;

import it.unicam.cs.mpgc.rpg123283.application.GameService;
import it.unicam.cs.mpgc.rpg123283.domain.CardCatalog;
import it.unicam.cs.mpgc.rpg123283.persistence.JsonCardCatalog;
import it.unicam.cs.mpgc.rpg123283.persistence.JsonGameStateRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private GameService gameService;
    private CardCatalog cardCatalog;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
        this.cardCatalog = new JsonCardCatalog();
        // Inizializziamo il Service iniettando il nuovo EventBus globale
        this.gameService = new GameService(
                new JsonGameStateRepository("savegame.json"),
                GameEventBus.getInstance(),
                cardCatalog
        );
    }

    public GameService getGameService() {
        return gameService;
    }

    public CardCatalog getCardCatalog() {
        return cardCatalog;
    }

    public void switchScene(String fxmlPath) {
        try {
            GameEventBus.getInstance().clearSubscribers();
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