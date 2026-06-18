package it.unicam.cs.mpgc.rpg.matricola.infrastructure;

import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.GameEvent;
import it.unicam.cs.mpgc.rpg.matricola.persistence.JsonGameStateRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager implements EventPublisher {
    private static SceneManager instance;
    private Stage primaryStage;
    private GameService gameService;
    private EventPublisher currentActiveController;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
        // Inizializziamo il service globale passando THIS come EventPublisher (Mediator)
        this.gameService = new GameService(new JsonGameStateRepository("savegame.json"), this);
    }

    public GameService getGameService() {
        return gameService;
    }

    // Il controller appena caricato si "iscrive" per ricevere gli eventi
    public void subscribe(EventPublisher controller) {
        this.currentActiveController = controller;
    }

    @Override
    public void publish(GameEvent event) {
        if (currentActiveController != null) {
            currentActiveController.publish(event);
        }
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 1000, 750));
            } else {
                primaryStage.getScene().setRoot(root); // Scambio a caldo fluido
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}