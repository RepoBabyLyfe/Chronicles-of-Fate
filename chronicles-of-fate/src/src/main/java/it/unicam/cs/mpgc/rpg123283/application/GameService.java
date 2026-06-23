package it.unicam.cs.mpgc.rpg123283.application;

import it.unicam.cs.mpgc.rpg123283.application.events.*;
import it.unicam.cs.mpgc.rpg123283.domain.*;
import it.unicam.cs.mpgc.rpg123283.domain.Character;

import java.util.List;
import java.util.logging.Logger;

/**
 * Facade dell'Application Layer. Delega al CombatOrchestrator e al ProfileManager,
 * mantenendo un'interfaccia pubblica stabile per la Presentazione.
 */
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());

    private final ProfileManager profileManager;
    private final CombatOrchestrator combatOrchestrator;

    private CombatResult lastCombatResult;

    public GameService(GameStateRepository repository, EventPublisher eventPublisher, CardCatalog cardCatalog) {
        this.profileManager = new ProfileManager(repository);
        this.combatOrchestrator = new CombatOrchestrator(eventPublisher, cardCatalog);
    }

    //inizializzop una nuova partita da zero
    public void startNewGame(Character player, Character enemy) {
        combatOrchestrator.startNewGame(player, enemy, profileManager.getPlayerProfile());
    }

    /**
     * Salva il profilo del giocatore (frammenti + carte sbloccate).
     * Sicuro da chiamare in qualsiasi momento, anche fuori dal combattimento.
     */
    public void saveProfile() {
        profileManager.saveProfile();
    }

    /**
     * Salva lo stato completo: profilo + stato del combattimento in corso.
     * Da chiamare SOLO se c'è un combattimento attivo.
     */
    public void saveGame() {
        profileManager.saveGame(combatOrchestrator.getCombatManager());
    }

    /**
     * Carica una partita precedente e ripristina il profilo giocatore.
     * Ripristina SOLO se il salvataggio contiene dati di combattimento.
     */
    public void loadGame(Character playerBase) {
        try {
            GameState state = profileManager.loadGameState();
            combatOrchestrator.loadCombat(playerBase, state, profileManager.getPlayerProfile());
        } catch (Exception e) {
            LOGGER.warning("Errore durante il caricamento: " + e.getMessage());
        }
    }

    /**
     * Finalizza il combattimento: assegna ricompense e salva il profilo.
     * @param result Il risultato del combattimento concluso.
     */
    public void endCombat(CombatResult result) {
        this.lastCombatResult = combatOrchestrator.endCombat(result, profileManager.getPlayerProfile());
        profileManager.saveProfile();
    }

    public CombatResult getLastCombatResult() {
        return lastCombatResult;
    }

    public CombatManager getCombatManager() {
        return combatOrchestrator.getCombatManager();
    }

    public void setCustomDeckRecipe(List<Card> recipe) {
        combatOrchestrator.setCustomDeckRecipe(recipe);
    }

    public List<Card> getCustomDeckRecipe() {
        return combatOrchestrator.getCustomDeckRecipe(profileManager.getPlayerProfile());
    }

    public Deck getCustomDeck() {
        return combatOrchestrator.getCustomDeck(profileManager.getPlayerProfile());
    }

    public PlayerProfile getPlayerProfile() {
        return profileManager.getPlayerProfile();
    }

    public void setPlayerProfile(PlayerProfile profile) {
        profileManager.setPlayerProfile(profile);
    }

    public void wipeProfile() {
        profileManager.wipeProfile();
        combatOrchestrator.clearCustomDeckRecipe();
        combatOrchestrator.clearCombat();
    }

    //permette di giocare una carta nascondendo il CombatManager
    public boolean playCard(Card card, Targetable target) {
        return combatOrchestrator.playCard(card, target);
    }

    //permette di comprare una carta dal negozio
    public boolean buyCard(Card card) {
        return profileManager.buyCard(card);
    }
}