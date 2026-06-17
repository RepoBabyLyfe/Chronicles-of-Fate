package it.unicam.cs.mpgc.rpg.matricola.presentation;

import it.unicam.cs.mpgc.rpg.matricola.application.GameService;
import it.unicam.cs.mpgc.rpg.matricola.application.events.*;
import it.unicam.cs.mpgc.rpg.matricola.persistence.JsonGameStateRepository;
import it.unicam.cs.mpgc.rpg.matricola.domain.*;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class MainController implements EventPublisher {


    @FXML private VBox mainMenuPane;
    @FXML private BorderPane combatPane;
    @FXML private Label playerHpLabel;
    @FXML private Label playerFocusLabel;
    @FXML private Label enemyHpLabel;
    @FXML private Label systemLogLabel;
    @FXML private ProgressBar playerHpBar;
    @FXML private ProgressBar enemyHpBar;
    @FXML private HBox handContainer;

    private GameService gameService;
    private Character player;
    private Character enemy;
    private Deck playerDeck;

    @FXML
    public void initialize() {
        JsonGameStateRepository repo = new JsonGameStateRepository("savegame.json");
        this.gameService = new GameService(repo, this);

        // Setup iniziale ma non avviamo ancora niente a schermo
        setupGameEntities();
    }

    private void setupGameEntities() {
        this.player = new Character("Tessitore di Fati", 30, 5) {};
        this.enemy = new Character("Avatar dell'Entropia", 50, 0) {};

        Rollable d6 = new StandardDice(6, RandomGenerator.getDefault());

        CardEffect effettoInstabile = new TieredDamageEffect(List.of(
                new DamageTier(1, 2, 2), new DamageTier(3, 5, 6), new DamageTier(6, 6, 12)
        ));
        Card colpoInstabile = new Card("Colpo Instabile", 1, d6, effettoInstabile);

        CardEffect effettoLieve = new TieredDamageEffect(List.of(
                new DamageTier(1, 6, 4)
        ));
        Card colpoSicuro = new Card("Colpo Sicuro", 2, d6, effettoLieve);

        List<Card> initialCards = new ArrayList<>();
        for(int i=0; i<7; i++) initialCards.add(colpoInstabile);
        for(int i=0; i<3; i++) initialCards.add(colpoSicuro);

        this.playerDeck = new Deck(initialCards);

        if(handContainer != null) handContainer.getChildren().clear();
    }

    // ==========================================
    // NAVIGAZIONE E MENU PRINCIPALE
    // ==========================================

    @FXML
    public void onStartGameClicked() {
        // Transizione UI
        mainMenuPane.setVisible(false);
        combatPane.setVisible(true);

        setupGameEntities(); // Resetta sempre prima di una nuova partita
        gameService.startNewGame(player, enemy);
        logToScreen("L'arena ti attende! Peschi 3 carte.");

        playerDeck.drawCards(3);
        renderHand();
        updateUI();
    }

    @FXML
    public void onLoadGameFromMenuClicked() {
        setupGameEntities(); // Crea i gusci vuoti

        // Prova a caricare dal JSON
        try {
            gameService.loadGame(player, enemy);
            // Se va a buon fine, cambiamo schermata
            mainMenuPane.setVisible(false);
            combatPane.setVisible(true);

            logToScreen("Bentornato nell'arena. La battaglia continua.");
            playerDeck.drawCards(3);
            renderHand();
            updateUI();
        } catch (Exception e) {
            mostraPopup("Errore di Caricamento", "Nessun salvataggio trovato o file corrotto.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void onBackToMenuClicked() {
        combatPane.setVisible(false);
        mainMenuPane.setVisible(true);
    }

    @FXML
    public void onExitClicked() {
        Platform.exit();
        System.exit(0);
    }

    // ==========================================
    // LOGICA DI COMBATTIMENTO (Arena)
    // ==========================================

    private void renderHand() {
        if (handContainer == null) return;
        handContainer.getChildren().clear();

        for (Card card : playerDeck.getHand()) {
            VBox cardUI = new VBox(10);
            cardUI.getStyleClass().add("card-container");
            cardUI.setAlignment(Pos.TOP_CENTER);

            Label costLabel = new Label(card.getManaCost() + " Focus");
            costLabel.getStyleClass().add("card-cost");

            Label titleLabel = new Label(card.getName());
            titleLabel.getStyleClass().add("card-title");

            cardUI.setOnMouseClicked(e -> playSpecificCard(card));

            cardUI.getChildren().addAll(costLabel, titleLabel);
            handContainer.getChildren().add(cardUI);
        }
    }

    private void playSpecificCard(Card card) {
        if (!player.isAlive() || !enemy.isAlive()) return;

        if (gameService.getCombatManager() != null) {
            int hpPrima = enemy.getCurrentHp();

            boolean successo = gameService.getCombatManager().playCard(card, enemy);
            if (successo) {
                int dannoFatto = hpPrima - enemy.getCurrentHp();
                logToScreen("🃏 Giocata: " + card.getName() + " -> Sottratto " + dannoFatto + " AURA al nemico!");

                playerDeck.discardCard(card);
                renderHand();
                updateUI();
                checkGameOver();
            } else {
                logToScreen("Azione negata (Focus insufficiente o non è il tuo turno).");
            }
        }
    }

    @FXML
    public void onNextPhaseClicked() {
        if (!player.isAlive() || !enemy.isAlive() || gameService.getCombatManager() == null) return;

        int hpPrima = player.getCurrentHp();
        gameService.getCombatManager().nextPhase();

        int dannoSubito = hpPrima - player.getCurrentHp();
        if (dannoSubito > 0) {
            logToScreen("FAAAAHHHH! Il Boss ti ha attaccato! Hai perso " + dannoSubito + " Aura.");
        } else {
            logToScreen("Fase successiva avviata.");
        }

        playerDeck.discardHand();
        playerDeck.drawCards(3);
        renderHand();
        updateUI();
        checkGameOver();
    }

    @FXML
    public void onRestartClicked() {
        onStartGameClicked(); // Riusiamo la logica di start per resettare tutto
        logToScreen("Il loop si ripete... Nuovo scontro iniziato.");
    }

    @FXML
    public void onSaveClicked() {
        gameService.saveGame();
        logToScreen("I tuoi progressi sono stati scritti nelle cronache.");
    }

    @Override
    public void publish(GameEvent event) {
        Platform.runLater(() -> {
            if (event instanceof HpChangedEvent e) {
                if (e.character() == player) {
                    playerHpLabel.setText("AURA Eroe: " + e.currentHp() + "/" + e.maxHp());
                } else {
                    enemyHpLabel.setText("AURA Boss: " + e.currentHp() + "/" + e.maxHp());
                }
            }
        });
    }

    private void updateUI() {
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

    private void logToScreen(String message) {
        if (systemLogLabel != null) systemLogLabel.setText(message);
        System.out.println("[GUI LOG] " + message);
    }

    private void checkGameOver() {
        if (!player.isAlive()) {
            logToScreen("💀FAAAAHHH!!! HAI PERSO... L'Entropia ha vinto.");
            mostraPopup("Sconfitta", "Il Tessitore di Fati è caduto. Il multiverso è collassato.", Alert.AlertType.ERROR);
        } else if (!enemy.isAlive()) {
            logToScreen("🏆 HAI VINTO!");
            mostraPopup("Vittoria Suprema!", "Hai sconfitto l'Avatar dell'Entropia e ripristinato l'ordine!", Alert.AlertType.INFORMATION);
        }
    }

    private void mostraPopup(String titolo, String messaggio, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo, messaggio, ButtonType.OK);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}