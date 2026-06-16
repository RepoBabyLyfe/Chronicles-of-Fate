package it.unicam.cs.mpgc.rpg.matricola.application;

import it.unicam.cs.mpgc.rpg.matricola.application.events.EventPublisher;
import it.unicam.cs.mpgc.rpg.matricola.application.events.HpChangedEvent;
import it.unicam.cs.mpgc.rpg.matricola.domain.Character;
import it.unicam.cs.mpgc.rpg.matricola.domain.CombatManager;

/**
 * Orchestratore dell'Application Layer. Gestisce il flusso principale
 * nascondendo la complessità del Dominio alla futura Interfaccia Grafica.
 */
public class GameService {

    private final GameStateRepository repository;
    private final EventPublisher eventPublisher;
    private CombatManager combatManager;

    public GameService(GameStateRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Inizializza una nuova partita da zero.
     */
    public void startNewGame(Character player, Character enemy) {
        this.combatManager = new CombatManager(player, enemy);
        this.combatManager.startCombat();
        System.out.println("Nuova partita avviata!");

        eventPublisher.publish(new HpChangedEvent(player, player.getCurrentHp(), player.getMaxHp()));
        eventPublisher.publish(new HpChangedEvent(enemy, enemy.getCurrentHp(), enemy.getMaxHp()));
    }

    /**
     * Salva lo stato attuale della partita.
     */
    public void saveGame() {
        if (combatManager == null) {
            System.err.println("Nessuna partita attiva da salvare.");
            return;
        }

        GameState state = new GameState(
                combatManager.getPlayer().getCurrentHp(),
                combatManager.getPlayer().getCurrentFocus(),
                combatManager.getEnemy().getCurrentHp()
        );

        try {
            repository.save(state);
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Carica una partita precedente, sovrascrivendo gli HP correnti.
     */
    public void loadGame(Character playerBase, Character enemyBase) {
        try {
            GameState state = repository.load();

            // Applichiamo i dati salvati ai personaggi base passati come template
            int playerDamageToApply = playerBase.getMaxHp() - state.playerHp();
            playerBase.takeDamage(playerDamageToApply);

            // Azzeriamo e ripristiniamo il focus
            playerBase.consumeFocus(playerBase.getCurrentFocus());
            playerBase.restoreFocus(state.playerFocus());

            int enemyDamageToApply = enemyBase.getMaxHp() - state.enemyHp();
            enemyBase.takeDamage(enemyDamageToApply);

            // Ricreiamo il manager con i dati aggiornati
            this.combatManager = new CombatManager(playerBase, enemyBase);
            this.combatManager.startCombat();

            eventPublisher.publish(new HpChangedEvent(playerBase, playerBase.getCurrentHp(), playerBase.getMaxHp()));
            eventPublisher.publish(new HpChangedEvent(enemyBase, enemyBase.getCurrentHp(), enemyBase.getMaxHp()));

        } catch (Exception e) {
            System.err.println("Errore durante il caricamento: " + e.getMessage());
        }
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }
}