package it.unicam.cs.mpgc.rpg.matricola.domain;

public class Shop {

    /**
     * Tenta di vendere una carta al profilo del giocatore.
     * @return true se l'acquisto va a buon fine, false se mancano fondi o se è già posseduta.
     */
    public boolean buyCard(Card card, PlayerProfile profile) {
        // 1. Controlla se il giocatore ha già la carta
        if (profile.hasCard(card.getName())) {
            return false;
        }

        // 2. Tenta di scalare i frammenti
        if (profile.spendFragments(card.getPrice())) {
            // 3. Sblocca la carta nel profilo
            profile.unlockCard(card.getName());
            return true;
        }

        return false;
    }
}