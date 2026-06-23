package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.domain.CombatOutcome;
import it.unicam.cs.mpgc.rpg123283.domain.CombatResult;
import it.unicam.cs.mpgc.rpg123283.infrastructure.SceneManager;
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
    @FXML
    private javafx.scene.control.Button playAgainButton;
    @FXML
    private javafx.scene.control.Button compendiumButton;

    private SpaceBackgroundEngine spaceBackgroundEngine;

    @FXML
    public void initialize() {
        if (spaceCanvas != null && rootPane != null) {
            this.spaceBackgroundEngine = SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }

        //eecupero il risultato dal GameService
        CombatResult result = SceneManager.getInstance().getGameService().getLastCombatResult();
        if (result != null) {
            displayResult(result);
        }

        playEntryAnimation();
    }

    private void displayResult(CombatResult result) {
        if (result.isVictory()) {
            int defeated = SceneManager.getInstance().getGameService().getPlayerProfile().getDefeatedBosses();
            if (defeated < 3) {
                outcomeTitle.setText("VITTORIA");
                outcomeSubtitle.setText("Hai sconfitto il Boss!\nHai ottenuto le sue carte nel tuo grimorio.");
                playAgainButton.setText("[ PROSSIMO BOSS ]");
            } else {
                outcomeTitle.setText("CAMPAGNA COMPLETATA");
                outcomeSubtitle.setText("Torna indietro nel tempo con la conoscenza che hai appreso adesso e cambia le altre linee temporali.");
                playAgainButton.setText("[ NUOVA PARTITA+ ]");
                if (compendiumButton != null) {
                    compendiumButton.setVisible(false);
                    compendiumButton.setManaged(false);
                }
                //reset dei boss per la run successiva
                SceneManager.getInstance().getGameService().getPlayerProfile().resetDefeatedBosses();
                SceneManager.getInstance().getGameService().saveProfile();
            }
            outcomeTitle.getStyleClass().add("victory-title");
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
    public void onGoToCompendiumClicked() {
        SceneManager.getInstance().switchScene("/deck_builder_view.fxml");
    }

    @FXML
    public void onPlayAgainClicked() {
        it.unicam.cs.mpgc.rpg123283.application.GameService service = SceneManager.getInstance().getGameService();
        it.unicam.cs.mpgc.rpg123283.domain.Character player = it.unicam.cs.mpgc.rpg123283.application.GameFactory.createPlayer();
        int defeatedBosses = service.getPlayerProfile().getDefeatedBosses();
        
        //se la campagna è completa defeatedBosses == 0 perché è stato resettato in displayResult
        //oppure si va avanti normalmente
        it.unicam.cs.mpgc.rpg123283.domain.Character enemy = it.unicam.cs.mpgc.rpg123283.application.GameFactory.createEnemy(defeatedBosses);
        service.startNewGame(player, enemy);
        SceneManager.getInstance().switchScene("/combat_view.fxml");
    }
}
