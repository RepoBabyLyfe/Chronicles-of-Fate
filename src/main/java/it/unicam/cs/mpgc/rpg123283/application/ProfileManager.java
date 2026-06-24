package it.unicam.cs.mpgc.rpg123283.application;

import it.unicam.cs.mpgc.rpg123283.domain.*;

import java.util.Set;
import java.util.logging.Logger;

/**
 *gestisce il profilo del giocatore e la persistenza
 *caricamento, salvataggio, azzeramento profilo e acquisti nello shop
 */
public class ProfileManager {

    private static final Logger LOGGER = Logger.getLogger(ProfileManager.class.getName());

    private static final Set<String> BASE_CARDS = Set.of(
            "Risonanza Eterea", "Fenditura Quantica",
            "Sovraccarico Biologico", "Innesto Simbiotico", "Assimilazione Oscura"
    );

    private final GameStateRepository repository;
    private PlayerProfile playerProfile;

    public ProfileManager(GameStateRepository repository) {
        this.repository = repository;
        this.playerProfile = loadProfileFromSave();
    }

    //tenta di caricare il profilo giocatore dal file di salvataggio
    //se non esiste o è corrotto, restituisce un profilo base con le 5 carte starter
    private PlayerProfile loadProfileFromSave() {
        try {
            if (repository.saveExists()) {
                GameState state = repository.load();
                LOGGER.info("Profilo caricato dal salvataggio: Frammenti("
                        + state.etherFragments() + "), Carte(" + state.unlockedCards().size() + ")");
                return new PlayerProfile(state.etherFragments(), state.unlockedCards(), state.defeatedBosses());
            }
        } catch (Exception e) {
            LOGGER.warning("Errore nel caricamento del profilo: " + e.getMessage()
                    + " — Utilizzo profilo base.");
        }
        return new PlayerProfile(0, BASE_CARDS);
    }

    //salva il profilo del giocatore (frammenti + carte sbloccate)
    //sicuro da chiamare in qualsiasi momento, anche fuori dal combattimento
    public void saveProfile() {
        GameState state = GameState.profileOnly(
                playerProfile.getEtherFragments(),
                playerProfile.getUnlockedCards(),
                playerProfile.getDefeatedBosses()
        );
        persistState(state);
    }

    //salva lo stato completo: profilo + stato del combattimento in corso
    public void saveGame(CombatManager combatManager) {
        if (combatManager == null) {
            saveProfile();
            return;
        }

        GameState state = GameState.withCombat(
                combatManager.getPlayer().getCurrentHp(),
                combatManager.getPlayer().getCurrentFocus(),
                combatManager.getEnemy().getCurrentHp(),
                playerProfile.getEtherFragments(),
                playerProfile.getUnlockedCards(),
                playerProfile.getDefeatedBosses()
        );
        persistState(state);
    }

    //carica lo stato completo dalla persistenza
    public GameState loadGameState() throws Exception {
        GameState state = repository.load();
        this.playerProfile = new PlayerProfile(state.etherFragments(), state.unlockedCards(), state.defeatedBosses());
        return state;
    }

    //azzera il profilo del giocatore e lo salva
    public void wipeProfile() {
        this.playerProfile = new PlayerProfile(0, BASE_CARDS);
        saveProfile();
    }

    //facade, permette di comprare una carta dal negozio
    public boolean buyCard(Card card) {
        Shop shop = new Shop();
        return shop.buyCard(card, playerProfile);
    }

    public PlayerProfile getPlayerProfile() {
        return this.playerProfile;
    }

    public void setPlayerProfile(PlayerProfile profile) {
        this.playerProfile = profile;
    }

    private void persistState(GameState state) {
        try {
            repository.save(state);
            LOGGER.info("Salvataggio completato: Frammenti("
                    + playerProfile.getEtherFragments() + "), Carte("
                    + playerProfile.getUnlockedCards().size() + ")");
        } catch (Exception e) {
            LOGGER.warning("Errore durante il salvataggio: " + e.getMessage());
        }
    }
}
