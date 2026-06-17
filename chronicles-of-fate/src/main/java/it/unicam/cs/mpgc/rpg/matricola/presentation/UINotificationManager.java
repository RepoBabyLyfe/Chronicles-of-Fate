package it.unicam.cs.mpgc.rpg.matricola.presentation;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

/**
 * Gestisce i log di sistema, la messaggistica a schermo e i pop-up modali.
 */
public class UINotificationManager {
    private final Label systemLogLabel;

    public UINotificationManager(Label systemLogLabel) {
        this.systemLogLabel = systemLogLabel;
    }

    public void logMessage(String message) {
        if (systemLogLabel != null) {
            Platform.runLater(() -> systemLogLabel.setText(message));
        }
        System.out.println("[GUI LOG] " + message);
    }

    public void showGameOverPopup(String title, String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }
}