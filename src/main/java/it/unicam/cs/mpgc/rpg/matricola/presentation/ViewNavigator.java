package it.unicam.cs.mpgc.rpg.matricola.presentation;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Gestisce la navigazione e il routing visivo tra le schermate dell'applicazione.
 */
public class ViewNavigator {
    private final VBox mainMenuPane;
    private final BorderPane combatPane;

    public ViewNavigator(VBox mainMenuPane, BorderPane combatPane) {
        this.mainMenuPane = mainMenuPane;
        this.combatPane = combatPane;
    }

    public void showMainLobby() {
        if (mainMenuPane != null && combatPane != null) {
            combatPane.setVisible(false);
            mainMenuPane.setVisible(true);
        }
    }

    public void showCombatArena() {
        if (mainMenuPane != null && combatPane != null) {
            mainMenuPane.setVisible(false);
            combatPane.setVisible(true);
        }
    }
}