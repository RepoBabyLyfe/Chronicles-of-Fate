package it.unicam.cs.mpgc.rpg.matricola.infrastructure;

import javafx.application.Application;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.getInstance().init(primaryStage);
        primaryStage.setTitle("Chronicles of Fate");
        SceneManager.getInstance().switchScene("/menu_view.fxml");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}