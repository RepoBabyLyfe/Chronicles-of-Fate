package it.unicam.cs.mpgc.rpg123283.presentation;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DiceAnimator {

    private final VBox diceOverlay;
    private final Label diceValueLabel;
    private final String[] diceFaces = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};

    public DiceAnimator(VBox diceOverlay, Label diceValueLabel) {
        this.diceOverlay = diceOverlay;
        this.diceValueLabel = diceValueLabel;
    }

    /**
     * Svolge l'intera sequenza visiva del dado: attesa del click,
     * rotazione e raddrizzamento finale con delay.
     */
    public void playRollSequence(int realRoll, Runnable onSequenceFinished) {
        diceOverlay.setVisible(true);
        diceValueLabel.setText("🎲");

        Timeline rollAnimation = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {
                    int randomIndex = (int) (Math.random() * 6);
                    diceValueLabel.setText(diceFaces[randomIndex]);
                    diceValueLabel.setRotate(diceValueLabel.getRotate() + 45);
                })
        );
        rollAnimation.setCycleCount(10);

        rollAnimation.setOnFinished(e -> {
            diceValueLabel.setText(diceFaces[realRoll - 1]);
            diceValueLabel.setRotate(0);

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                diceOverlay.setVisible(false);
                onSequenceFinished.run(); //callback nel controller
            });
            pause.play();
        });

        diceOverlay.setOnMouseClicked(event -> {
            diceOverlay.setOnMouseClicked(null);
            rollAnimation.play();
        });
    }
}