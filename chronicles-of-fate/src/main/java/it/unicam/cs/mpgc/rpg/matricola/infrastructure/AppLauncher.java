package it.unicam.cs.mpgc.rpg.matricola.infrastructure;

import javafx.application.Application;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.getInstance().init(primaryStage);
        primaryStage.setTitle("Chronicles of Fate");
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);

        SceneManager.getInstance().switchScene("/menu_view.fxml");
        primaryStage.show();
    }
}