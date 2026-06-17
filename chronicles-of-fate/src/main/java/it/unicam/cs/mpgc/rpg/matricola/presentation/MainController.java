package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.application.GameFactory;
import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.application.events.*;
import it.unicam.cs.mpgc.rpg.matricola.persistence.JsonGameStateRepository;
import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas; // Unico import pulito

public class MainController implements EventPublisher {

    @FXML private StackPane rootPane;
    @FXML private VBox mainMenuPane;
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

    private GameService gameService;
    private Character player;
    private Character enemy;
    private Deck playerDeck;
    private boolean isAnimating = false;

    private DiceAnimator diceAnimator;
    private HandViewRenderer handRenderer;
    private ViewNavigator viewNavigator;
    private UINotificationManager notificationManager;
    private CombatPresenter combatPresenter;
    private SpaceBackgroundEngine spaceBackgroundEngine;

    @FXML
    public void initialize() {
        JsonGameStateRepository repo = new JsonGameStateRepository("savegame.json");
        this.gameService = new GameService(repo, this);

        this.notificationManager = new UINotificationManager(systemLogLabel);
        this.diceAnimator = new DiceAnimator(diceOverlay, diceValueLabel);
        this.handRenderer = new HandViewRenderer(handContainer, this::playSpecificCard);
        this.viewNavigator = new ViewNavigator(mainMenuPane, combatPane);

        this.combatPresenter = new CombatPresenter(
                playerHpLabel, playerFocusLabel, enemyHpLabel,
                playerHpBar, enemyHpBar, notificationManager
        );

        setupGameEntities();

        // --- BINDING RESPONSIVE & ENGINE SPAZIALE ---
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

    private void setupGameEntities() {
        this.player = GameFactory.createPlayer();
        this.enemy = GameFactory.createEnemy();
        this.playerDeck = GameFactory.createStartingDeck();

        if (combatPresenter != null) {
            combatPresenter.setCharacters(player, enemy);
        }

        if (handContainer != null) handContainer.getChildren().clear();
    }

    @FXML
    public void onStartGameClicked() {
        viewNavigator.showCombatArena();
        setupGameEntities();
        gameService.startNewGame(player, enemy);
        notificationManager.logMessage("L'arena ti attende! Peschi 3 carte.");
        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();
    }

    @FXML
    public void onLoadGameFromMenuClicked() {
        setupGameEntities();
        try {
            gameService.loadGame(player, enemy);
            viewNavigator.showCombatArena();
            notificationManager.logMessage("Bentornato nell'arena. La battaglia continua.");
            playerDeck.drawCards(3);
            renderHand();
            combatPresenter.updateUI();
        } catch (Exception e) {
            notificationManager.showGameOverPopup("Errore di Caricamento", "Nessun salvataggio trovato o file corrotto.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void onBackToMenuClicked() {
        viewNavigator.showMainLobby();
    }

    @FXML
    public void onExitClicked() {
        Platform.exit();
        System.exit(0);
    }

    public void renderHand() {
        if (playerDeck == null) return;
        handRenderer.renderHand(playerDeck.getHand(), player.getCurrentFocus(), isAnimating);
    }

    private void playSpecificCard(Card card) {
        if (!player.isAlive() || !enemy.isAlive()) return;
        if (gameService.getCombatManager() == null) return;

        isAnimating = true;
        int hpPrima = enemy.getCurrentHp();
        boolean successo = gameService.getCombatManager().playCard(card, enemy);

        if (!successo) {
            isAnimating = false;
            notificationManager.logMessage("Azione negata (Focus insufficiente).");
            return;
        }

        int realRoll = gameService.getCombatManager().getLastDiceRoll();
        int dannoFatto = hpPrima - enemy.getCurrentHp();

        notificationManager.logMessage("Innesco: " + card.getName() + " -> Clicca lo schermo per lanciare.");

        diceAnimator.playRollSequence(realRoll, () -> {
            isAnimating = false;
            combatPresenter.updateUI();

            notificationManager.logMessage("🃏 Colpo a segno! Dado: " + realRoll + " -> Inflitti " + dannoFatto + " danni!");

            if (dannoFatto > 0) {
                combatPresenter.playBossDamageAnimation();
            }

            playerDeck.discardCard(card);
            renderHand();
            combatPresenter.checkGameOver();
        });
    }

    @FXML
    public void onNextPhaseClicked() {
        if (!player.isAlive() || !enemy.isAlive() || gameService.getCombatManager() == null) return;

        int hpPrima = player.getCurrentHp();
        gameService.getCombatManager().nextPhase();

        int dannoSubito = hpPrima - player.getCurrentHp();
        if (dannoSubito > 0) {
            notificationManager.logMessage("FAAAAHHHH! Il Boss ti ha attaccato! Hai perso " + dannoSubito + " Aura.");
            combatPresenter.playPlayerDamageAnimation();
        } else {
            notificationManager.logMessage("Fase successiva avviata.");
        }

        playerDeck.discardHand();
        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();
        combatPresenter.checkGameOver();
    }

    @FXML
    public void onRestartClicked() {
        onStartGameClicked();
        notificationManager.logMessage("Il loop si ripete... Nuovo scontro iniziato.");
    }

    @FXML
    public void onSaveClicked() {
        gameService.saveGame();
        notificationManager.logMessage("I tuoi progressi sono stati scritti nelle cronache.");
    }

    @Override
    public void publish(GameEvent event) {
        if (isAnimating) return;
        if (event instanceof HpChangedEvent hpEvent) {
            combatPresenter.handleHpChange(hpEvent);
        }
    }
}