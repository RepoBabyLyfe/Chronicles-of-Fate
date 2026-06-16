package it.unicam.cs.mpgc.rpg.matricola.infrastructure;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica il file FXML dalla cartella resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_view.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Chronicles of Fate");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}