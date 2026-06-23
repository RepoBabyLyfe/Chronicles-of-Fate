package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.domain.CombatOutcome;
import it.unicam.cs.mpgc.rpg.matricola.domain.CombatResult;
import it.unicam.cs.mpgc.rpg.matricola.infrastructure.SceneManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller della schermata di fine combattimento.
 * Mostra l'esito (vittoria/sconfitta), i frammenti guadagnati e le opzioni di
 * navigazione.
 */
public class VictoryController {

    @FXML
    private StackPane rootPane;
    @FXML
    private Canvas spaceCanvas;
    @FXML
    private Label outcomeTitle;
    @FXML
    private Label outcomeSubtitle;
    @FXML
    private Label fragmentsLabel;
    @FXML
    private VBox rewardsBox;
    @FXML
    private VBox buttonsBox;

    private SpaceBackgroundEngine spaceBackgroundEngine;

    @FXML
    public void initialize() {
        // sfondo spaziale
        if (spaceCanvas != null && rootPane != null) {
            this.spaceBackgroundEngine = SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }

        // eecupero il risultato dal GameService
        CombatResult result = SceneManager.getInstance().getGameService().getLastCombatResult();
        if (result != null) {
            displayResult(result);
        }

        playEntryAnimation();
    }

    private void displayResult(CombatResult result) {
        if (result.isVictory()) {
            outcomeTitle.setText("VITTORIA SUPREMA");
            outcomeTitle.getStyleClass().add("victory-title");
            outcomeSubtitle
                    .setText("L'Avatar dell'Entropia è stato annientato.\nL'ordine cosmico è stato ripristinato.");
            fragmentsLabel.setText("+ " + result.fragmentsEarned() + " Frammenti di Etere ✨");
        } else {
            outcomeTitle.setText("SCONFITTA");
            outcomeTitle.getStyleClass().add("defeat-title");
            outcomeSubtitle.setText("Il Tessitore di Fati è caduto.\nIl multiverso sprofonda nel caos...");
            fragmentsLabel.setText("Nessun frammento ottenuto");
            fragmentsLabel.getStyleClass().add("defeat-fragments");
        }
    }

    private void playEntryAnimation() {
        FadeTransition titleFade = new FadeTransition(Duration.millis(800), outcomeTitle);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);

        ScaleTransition titleScale = new ScaleTransition(Duration.millis(600), outcomeTitle);
        titleScale.setFromX(0.5);
        titleScale.setFromY(0.5);
        titleScale.setToX(1.0);
        titleScale.setToY(1.0);

        FadeTransition rewardsFade = new FadeTransition(Duration.millis(600), rewardsBox);
        rewardsFade.setFromValue(0);
        rewardsFade.setToValue(1);

        FadeTransition buttonsFade = new FadeTransition(Duration.millis(400), buttonsBox);
        buttonsFade.setFromValue(0);
        buttonsFade.setToValue(1);

        outcomeTitle.setOpacity(0);
        rewardsBox.setOpacity(0);
        buttonsBox.setOpacity(0);

        SequentialTransition sequence = new SequentialTransition(titleFade, rewardsFade, buttonsFade);
        titleScale.play();
        sequence.play();
    }

    @FXML
    public void onBackToMenuClicked() {
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }

    @FXML
    public void onGoToShopClicked() {
        SceneManager.getInstance().switchScene("/shop_view.fxml");
    }

    @FXML
    public void onPlayAgainClicked() {
        SceneManager.getInstance().switchScene("/deck_builder_view.fxml");
    }
}
