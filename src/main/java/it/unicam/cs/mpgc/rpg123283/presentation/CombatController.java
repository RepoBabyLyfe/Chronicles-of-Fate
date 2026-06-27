package it.unicam.cs.mpgc.rpg123283.presentation;

import it.unicam.cs.mpgc.rpg123283.application.GameFactory;
import it.unicam.cs.mpgc.rpg123283.application.GameService;
import it.unicam.cs.mpgc.rpg123283.application.events.*;
import it.unicam.cs.mpgc.rpg123283.domain.*;
import it.unicam.cs.mpgc.rpg123283.domain.Character;
import it.unicam.cs.mpgc.rpg123283.infrastructure.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.Optional;

public class CombatController implements EventPublisher {

    @FXML
    private StackPane rootPane;
    @FXML
    private BorderPane combatPane;
    @FXML
    private Label playerHpLabel;
    @FXML
    private Label enemyNameLabel;
    @FXML
    private Label playerFocusLabel;
    @FXML
    private Label enemyHpLabel;
    @FXML
    private Label systemLogLabel;
    @FXML
    private ProgressBar playerHpBar;
    @FXML
    private ProgressBar enemyHpBar;
    @FXML
    private HBox handContainer;
    @FXML
    private VBox diceOverlay;
    @FXML
    private Label diceValueLabel;
    @FXML
    private Canvas spaceCanvas;
    @FXML
    private javafx.scene.control.TextArea historyLogArea;
    @FXML
    private VBox enemyCardOverlay;
    @FXML
    private ImageView enemyCardImage;
    @FXML
    private Label enemyOverlayText;
    @FXML private VBox pauseMenuOverlay;
    @FXML private Label crystalCountLabel;

    private GameService gameService;
    private Character player;
    private Character enemy;
    private Deck playerDeck;
    private boolean isAnimating = false;
    private boolean isCombatOver = false;
    private EnemyCardAnimator enemyCardAnimator;

    private DiceAnimator diceAnimator;
    private HandViewRenderer handRenderer;
    private UINotificationManager notificationManager;
    private CombatPresenter combatPresenter;

    @FXML
    public void initialize() {
        this.gameService = SceneManager.getInstance().getGameService();

        it.unicam.cs.mpgc.rpg123283.infrastructure.GameEventBus.getInstance().subscribe(this);

        this.notificationManager = new UINotificationManager(systemLogLabel);
        this.diceAnimator = new DiceAnimator(diceOverlay, diceValueLabel);
        this.handRenderer = new HandViewRenderer(handContainer, 0.75, 0, this::playSpecificCard);

        this.combatPresenter = new CombatPresenter(
                playerHpLabel, playerFocusLabel, enemyHpLabel,
                playerHpBar, enemyHpBar, notificationManager
        );

        this.player = gameService.getCombatManager().getPlayer();
        this.enemy = gameService.getCombatManager().getEnemy();
        this.playerDeck = gameService.getCustomDeck();

        if (enemyNameLabel != null) {
            enemyNameLabel.setText(enemy.getName());
        }

        combatPresenter.setCharacters(player, enemy);
        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();

        if (spaceCanvas != null && rootPane != null) {
            SpaceBackgroundInitializer.setup(rootPane, spaceCanvas);
        }
        this.enemyCardAnimator = new EnemyCardAnimator(enemyCardOverlay, enemyCardImage, enemyOverlayText);

        updateCrystalLabel();
    }

    private void updateCrystalLabel() {
        if (crystalCountLabel != null && gameService != null && gameService.getPlayerProfile() != null) {
            crystalCountLabel.setText(String.valueOf(gameService.getPlayerProfile().getEtherFragments()));
        }
    }

    public void renderHand() {
        if (playerDeck == null) return;
        handRenderer.renderHand(playerDeck.getHand(), player.getCurrentFocus(), isAnimating);
    }

    private void playSpecificCard(Card card) {
        if (!player.isAlive() || !enemy.isAlive() || gameService.getCombatManager() == null) return;

        isAnimating = true;
        int enemyHpPrima = enemy.getCurrentHp();
        int playerHpPrima = player.getCurrentHp();
        int playerFocusPrima = player.getCurrentFocus();

        boolean successo = gameService.playCard(card, enemy);

        if (!successo) {
            isAnimating = false;
            notificationManager.logMessage("Azione negata (Focus insufficiente).");
            return;
        }

        int dannoBoss = enemyHpPrima - enemy.getCurrentHp();
        int deltaAura = player.getCurrentHp() - playerHpPrima;
        int deltaFocus = player.getCurrentFocus() - playerFocusPrima;

        if (card.requiresDice()) {
            int realRoll = gameService.getCombatManager().getLastDiceRoll();
            diceAnimator.playRollSequence(realRoll, () -> finishCardPlay(card, dannoBoss, deltaAura, deltaFocus, realRoll));
        } else {
            finishCardPlay(card, dannoBoss, deltaAura, deltaFocus, 0);
        }
    }

    private void finishCardPlay(Card card, int dannoBoss, int deltaAura, int deltaFocus, int roll) {
        isAnimating = false;

        StringBuilder messaggio = new StringBuilder("[EROE] Gioca " + card.getName() + " ");
        messaggio.append("(-").append(card.getManaCost()).append(" Focus). ");

        if (roll > 0) messaggio.append("Dado: ").append(roll).append(". ");

        if (dannoBoss > 0) messaggio.append("Infligge ").append(dannoBoss).append(" danni al Boss! ");

        if (deltaAura > 0) messaggio.append("Si cura di ").append(deltaAura).append(" AURA. ");
        else if (deltaAura < 0) messaggio.append("Subisce ").append(Math.abs(deltaAura)).append(" danni da contraccolpo. ");

        int focusGenerato = deltaFocus + card.getManaCost();
        if (focusGenerato > 0) messaggio.append("Genera ").append(focusGenerato).append(" Focus extra.");

        publish(new LogEvent(messaggio.toString().trim()));

        if (dannoBoss > 0) publish(new DamageTakenEvent(enemy, dannoBoss, true));
        if (deltaAura < 0) publish(new DamageTakenEvent(player, Math.abs(deltaAura), true));

        publish(new HpChangedEvent(enemy, enemy.getCurrentHp(), enemy.getMaxHp()));
        publish(new HpChangedEvent(player, player.getCurrentHp(), player.getMaxHp()));
        if (playerFocusLabel != null) {
            playerFocusLabel.setText("Focus: " + player.getCurrentFocus());
        }

        playerDeck.discardCard(card);
        renderHand();
        combatPresenter.updateUI();

        handleGameOverIfNeeded();
    }

    private void handleGameOverIfNeeded() {
        if (isCombatOver) return;

        Optional<CombatResult> result = combatPresenter.checkGameOver();
        result.ifPresent(combatResult -> {
            isCombatOver = true;
            gameService.endCombat(combatResult);

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> SceneManager.getInstance().switchScene("/victory_view.fxml"));
            pause.play();
        });
    }

    @FXML
    public void onNextPhaseClicked() {
        if (!player.isAlive() || !enemy.isAlive() || gameService.getCombatManager() == null) return;
        if (isAnimating) return;
        
        isAnimating = true;
        gameService.getCombatManager().nextPhase();

        publish(new HpChangedEvent(player, player.getCurrentHp(), player.getMaxHp()));
        publish(new HpChangedEvent(enemy, enemy.getCurrentHp(), enemy.getMaxHp()));
        if (playerFocusLabel != null) {
            playerFocusLabel.setText("Focus: " + player.getCurrentFocus());
        }
        combatPresenter.updateUI();
        handleGameOverIfNeeded();
    }

    @FXML
    public void onPauseClicked() {
        if (isAnimating) return;
        pauseMenuOverlay.setVisible(true);
        isAnimating = true;
    }

    @FXML
    public void onResumeClicked() {
        pauseMenuOverlay.setVisible(false);
        isAnimating = false;
    }



    @FXML
    public void onRestartClicked() {
        onResumeClicked();
        this.player = GameFactory.createPlayer();
        int defeatedBosses = gameService.getPlayerProfile().getDefeatedBosses();
        this.enemy = GameFactory.createEnemy(defeatedBosses);
        gameService.startNewGame(player, enemy);

        this.playerDeck = gameService.getCustomDeck();
        combatPresenter.setCharacters(player, enemy);

        playerDeck.drawCards(3);
        renderHand();
        combatPresenter.updateUI();
        notificationManager.logMessage("Il loop si ripete... Nuovo scontro iniziato.");
    }

    @FXML
    public void onSaveAndQuitClicked() {
        gameService.saveGame();
        notificationManager.logMessage("I tuoi progressi sono stati scritti nelle cronache.");
        onResumeClicked();
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }

    @FXML
    public void onSaveClicked() {
        gameService.saveGame();
        notificationManager.logMessage("I tuoi progressi sono stati scritti nelle cronache.");
        onResumeClicked();
    }

    @FXML
    public void onBackToMenuClicked() {
        SceneManager.getInstance().switchScene("/menu_view.fxml");
    }

    @Override
    public void publish(GameEvent event) {
        if (event instanceof HpChangedEvent hpEvent) {
            combatPresenter.handleHpChange(hpEvent);
        } else if (event instanceof LogEvent logEvent) {
            notificationManager.logMessage(logEvent.message());
            if (historyLogArea != null) historyLogArea.appendText("- " + logEvent.message() + "\n");
        } else if (event instanceof DamageTakenEvent damageEvent) {
            if (damageEvent.target() == enemy) combatPresenter.playBossDamageAnimation();
            else if (damageEvent.target() == player) combatPresenter.playPlayerDamageAnimation();
        } else if (event instanceof EnemyCardPlayedEvent enemyEvent) {
            isAnimating = true;
            enemyCardAnimator.playAnimation(enemyEvent.cardName(), enemyEvent.imagePath(), () -> {
                if (gameService.getCombatManager() != null) {
                    gameService.getCombatManager().nextPhase();
                }

                isAnimating = false;

                playerDeck.discardHand();
                playerDeck.drawCards(3);
                renderHand();
                
                if (playerFocusLabel != null) {
                    playerFocusLabel.setText("Focus: " + player.getCurrentFocus());
                }
                combatPresenter.updateUI();
                updateCrystalLabel();
                
                handleGameOverIfNeeded();
            });
        }
    }
}