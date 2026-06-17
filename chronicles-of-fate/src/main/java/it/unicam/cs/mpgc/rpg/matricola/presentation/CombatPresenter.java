package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.application.events.HpChangedEvent;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Presenter dedicato alla gestione dello stato visivo dell'arena di combattimento.
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

    /**
     * Associa o aggiorna i riferimenti ai personaggi correnti.
     */
    public void setCharacters(Character player, Character enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    /**
     * Aggiorna interamente lo stato visivo di etichette e barre di progresso.
     */
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

    /**
     * Gestisce l'evento di cambio degli HP ricevuto dal dominio.
     */
    public void handleHpChange(HpChangedEvent event) {
        Platform.runLater(() -> {
            if (event.character() == player) {
                if (playerHpLabel != null) playerHpLabel.setText("AURA Eroe: " + event.currentHp() + "/" + event.maxHp());
            } else if (event.character() == enemy) {
                if (enemyHpLabel != null) enemyHpLabel.setText("AURA Boss: " + event.currentHp() + "/" + event.maxHp());
            }
        });
    }

    /**
     * Valuta le condizioni di game over e gestisce i relativi popup.
     */
    public boolean checkGameOver() {
        if (!player.isAlive()) {
            notificationManager.logMessage("💀FAAAAHHH!!! HAI PERSO... L'Entropia ha vinto.");
            notificationManager.showGameOverPopup("Sconfitta", "Il Tessitore di Fati è caduto. Il multiverso è collassato.", Alert.AlertType.ERROR);
            return true;
        } else if (!enemy.isAlive()) {
            notificationManager.logMessage("🏆 HAI VINTO!");
            notificationManager.showGameOverPopup("Vittoria Suprema!", "Hai sconfitto l'Avatar dell'Entropia e ripristinato l'ordine!", Alert.AlertType.INFORMATION);
            return true;
        }
        return false;
    }

    /**
     * Attiva l'animazione di danno visivo (shake) per il Boss.
     */
    public void playBossDamageAnimation() {
        shakeNode(enemyHpBar);
        shakeNode(enemyHpLabel);
    }

    /**
     * Attiva l'animazione di danno visivo (shake) per il Giocatore.
     */
    public void playPlayerDamageAnimation() {
        shakeNode(playerHpBar);
        shakeNode(playerHpLabel);
    }

    /**
     * Metodo di supporto che fa vibrare orizzontalmente un nodo JavaFX.
     */
    private void shakeNode(Node node) {
        if (node == null) return;
        TranslateTransition shake = new TranslateTransition(Duration.millis(40), node);
        shake.setByX(12); // Spostamento di 12 pixel a destra e sinistra
        shake.setCycleCount(6); // Quante volte vibra
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> node.setTranslateX(0)); // Lo rimette al suo posto
        shake.play();
    }
}