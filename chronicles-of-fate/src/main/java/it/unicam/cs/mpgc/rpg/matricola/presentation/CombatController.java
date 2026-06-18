package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.application.GameFactory;
import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.application.events.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.infrastructure.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;

public class CombatController implements EventPublisher {

    @FXML private StackPane rootPane;
    @FXML private BorderPane combatPane;
    @FXML private Label playerHpLabel;
    @FXML private Label playerFocusLabel;
    @FXML private Label enemyHpLabel;
    @FXML private Label systemLogLabel;
    @FXML private ProgressBar playerHpBar;
    @FXML private ProgressBar enemyHpBar;
    @FXML private HBox handContainer;
    @FXML private VBox diceOverlay;
    @FXML private Label diceValueLabel;
    @FXML private Canvas spaceCanvas;
    @FXML private javafx.scene.control.TextArea historyLogArea;

    private GameService gameService;
    private Character player;
    private Character enemy;
    private Deck playerDeck;
    private boolean isAnimating = false;

    private DiceAnimator diceAnimator;
    private HandViewRenderer handRenderer;
    private UINotificationManager notificationManager;
    private CombatPresenter combatPresenter;
    private SpaceBackgroundEngine spaceBackgroundEngine;

    @FXML
    public void initialize() {
        this.gameService = SceneManager.getInstance().getGameService();
        SceneManager.getInstance().subscribe(this);

        if (gameService.getCombatManager() != null) {
            gameService.getCombatManager().subscribe(this);
        }

        this.notificationManager = new UINotificationManager(systemLogLabel);
        this.diceAnimator = new DiceAnimator(diceOverlay, diceValueLabel);
        this.handRenderer = new HandViewRenderer(handContainer, this::playSpecificCard);

        this.combatPresenter = new CombatPresenter(
                playerHpLabel, playerFocusLabel, enemyHpLabel,
                playerHpBar, enemyHpBar, notificationManager
        );

        this.player = gameService.getCombatManager().getPlayer();
        this.enemy = gameService.getCombatManager().getEnemy();
        this.playerDeck = GameFactory.createStartingDeck();

        combatPresenter.setCharacters(player, enemy);
        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();

        if (spaceCanvas != null && rootPane != null) {
            rootPane.widthProperty().addListener((obs, oldVal, newVal) -> spaceCanvas.setWidth(newVal.doubleValue()));
            rootPane.heightProperty().addListener((obs, oldVal, newVal) -> spaceCanvas.setHeight(newVal.doubleValue()));
            spaceCanvas.setWidth(rootPane.getPrefWidth());
            spaceCanvas.setHeight(rootPane.getPrefHeight());

            this.spaceBackgroundEngine = new SpaceBackgroundEngine(spaceCanvas);
            rootPane.setOnMouseMoved(e -> spaceBackgroundEngine.updateMouseCoordinates(e.getX(), e.getY()));
            this.spaceBackgroundEngine.start();
        }
    }

    public void renderHand() {
        if (playerDeck == null) return;
        handRenderer.renderHand(playerDeck.getHand(), player.getCurrentFocus(), isAnimating);
    }

    private void playSpecificCard(Card card) {
        if (!player.isAlive() || !enemy.isAlive() || gameService.getCombatManager() == null) return;

        isAnimating = true;
        int hpPrima = enemy.getCurrentHp();
        boolean successo = gameService.getCombatManager().playCard(card, enemy);

        if (!successo) {
            isAnimating = false;
            notificationManager.logMessage("Azione negata (Focus insufficiente).");
            return;
        }

        int dannoFatto = hpPrima - enemy.getCurrentHp();

        if (card.requiresDice()) {
            int realRoll = gameService.getCombatManager().getLastDiceRoll();
            diceAnimator.playRollSequence(realRoll, () -> finishCardPlay(card, dannoFatto));
        } else {
            finishCardPlay(card, dannoFatto);
        }
    }

    private void finishCardPlay(Card card, int dannoFatto) {
        isAnimating = false;

        if (dannoFatto > 0) {
            publish(new DamageTakenEvent(enemy, dannoFatto, true));
            publish(new LogEvent("Colpo critico! Inflitti " + dannoFatto + " danni all'Entropia!"));
        } else {
            publish(new LogEvent("L'Etere ha risposto al tuo comando. Effetto applicato!"));
        }

        playerDeck.discardCard(card);
        renderHand();
        combatPresenter.updateUI();
        combatPresenter.checkGameOver();
    }

    @FXML
    public void onNextPhaseClicked() {
        if (!player.isAlive() || !enemy.isAlive() || gameService.getCombatManager() == null) return;

        gameService.getCombatManager().nextPhase();
        playerDeck.discardHand();
        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();
        combatPresenter.checkGameOver();
    }

    @FXML
    public void onRestartClicked() {
        this.player = GameFactory.createPlayer();
        this.enemy = GameFactory.createEnemy();
        gameService.startNewGame(player, enemy);

        this.playerDeck = GameFactory.createStartingDeck();
        combatPresenter.setCharacters(player, enemy);
        gameService.getCombatManager().subscribe(this);

        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();
        notificationManager.logMessage("Il loop si ripete... Nuovo scontro iniziato.");
    }

    @FXML
    public void onSaveClicked() {
        gameService.saveGame();
        notificationManager.logMessage("I tuoi progressi sono stati scritti nelle cronache.");
    }

    @FXML
    public void onBackToMenuClicked() {
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }

    @Override
    public void publish(GameEvent event) {
        if (isAnimating) return;

        if (event instanceof HpChangedEvent hpEvent) {
            combatPresenter.handleHpChange(hpEvent);
        }
        else if (event instanceof LogEvent logEvent) {
            // 1. Aggiorna la scritta grande centrale
            notificationManager.logMessage(logEvent.message());

            // 2. NUOVO: Accoda il messaggio allo storico laterale e va a capo
            if (historyLogArea != null) {
                historyLogArea.appendText("- " + logEvent.message() + "\n");
            }
        }
        else if (event instanceof DamageTakenEvent damageEvent) {
            if (damageEvent.target() == enemy) {
                combatPresenter.playBossDamageAnimation();
            } else if (damageEvent.target() == player) {
                combatPresenter.playPlayerDamageAnimation();
            }
        }
    }
}