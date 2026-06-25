package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.application.events.HpChangedEvent;
import it.unicam.cs.mpgc.rpg123283.domain.Character;
import it.unicam.cs.mpgc.rpg123283.domain.CombatResult;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Optional;

/**
 * gestione dello stato visivo dell'arena di combattimento
 * restituisce dati strutturati al Controller
 */
public class CombatPresenter {

    private final Label playerHpLabel;
    private final Label playerFocusLabel;
    private final Label enemyHpLabel;
    private final ProgressBar playerHpBar;
    private final ProgressBar enemyHpBar;

    private Character player;
    private Character enemy;
    private final UINotificationManager notificationManager;

    public CombatPresenter(Label playerHpLabel, Label playerFocusLabel, Label enemyHpLabel,
                           ProgressBar playerHpBar, ProgressBar enemyHpBar,
                           UINotificationManager notificationManager) {
        this.playerHpLabel = playerHpLabel;
        this.playerFocusLabel = playerFocusLabel;
        this.enemyHpLabel = enemyHpLabel;
        this.playerHpBar = playerHpBar;
        this.enemyHpBar = enemyHpBar;
        this.notificationManager = notificationManager;
    }

    public void setCharacters(Character player, Character enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void updateUI() {
        if (player == null || enemy == null) return;

        if (playerHpLabel != null) playerHpLabel.setText("AURA Eroe: " + player.getCurrentHp() + "/" + player.getMaxHp());
        if (playerFocusLabel != null) playerFocusLabel.setText("Focus: " + player.getCurrentFocus());
        if (enemyHpLabel != null) enemyHpLabel.setText("AURA Boss: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp());

        if (playerHpBar != null) {
            playerHpBar.setProgress((double) player.getCurrentHp() / player.getMaxHp());
        }

        if (enemyHpBar != null) {
            enemyHpBar.setProgress((double) enemy.getCurrentHp() / enemy.getMaxHp());
        }
    }

    public void handleHpChange(HpChangedEvent event) {
        Platform.runLater(() -> {
            if (event.character() == player) {
                if (playerHpLabel != null) playerHpLabel.setText("AURA Eroe: " + event.currentHp() + "/" + event.maxHp());
            } else if (event.character() == enemy) {
                if (enemyHpLabel != null) enemyHpLabel.setText("AURA Boss: " + event.currentHp() + "/" + event.maxHp());
            }
        });
    }

    public Optional<CombatResult> checkGameOver() {
        if (player == null || enemy == null) return Optional.empty();

        if (!player.isAlive()) {
            notificationManager.logMessage("L'Entropia ha prevalso...");
            return Optional.of(CombatResult.defeat());
        } else if (!enemy.isAlive()) {
            notificationManager.logMessage("Il nemico è stato annientato!");
            return Optional.of(CombatResult.victory());
        }
        return Optional.empty();
    }

    public void playBossDamageAnimation() {
        shakeNode(enemyHpBar);
        shakeNode(enemyHpLabel);
    }

    public void playPlayerDamageAnimation() {
        shakeNode(playerHpBar);
        shakeNode(playerHpLabel);
    }

    private void shakeNode(Node node) {
        if (node == null) return;
        TranslateTransition shake = new TranslateTransition(Duration.millis(40), node);
        shake.setByX(12);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> node.setTranslateX(0));
        shake.play();
    }
}