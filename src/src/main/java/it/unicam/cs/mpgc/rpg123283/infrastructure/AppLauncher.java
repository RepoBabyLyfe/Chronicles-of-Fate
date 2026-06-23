package it.unicam.cs.mpgc.rpg123283.infrastructure;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.getInstance().init(primaryStage);
        primaryStage.setTitle("Chronicles of Fate");
        
        try {
            var logoStream = getClass().getResourceAsStream("/images/logo_1.png");
            if (logoStream != null) {
                primaryStage.getIcons().add(new Image(logoStream));
            }
        } catch (Exception ignored) {}

        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);

        SceneManager.getInstance().switchScene("/menu_view.fxml");
        primaryStage.show();
    }
}