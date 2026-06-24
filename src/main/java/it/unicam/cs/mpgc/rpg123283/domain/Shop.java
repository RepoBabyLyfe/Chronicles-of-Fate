package it.unicam.cs.mpgc.rpg123283.domain;

public class Shop {

    /**
     * tenta di vendere una carta al profilo del giocatore
     * @return true se l'acquisto va a buon fine, false se mancano fondi o se è già posseduta
     */
    public boolean buyCard(Card card, PlayerProfile profile) {
        //controllo se il giocatore ha già la carta
        if (profile.hasCard(card.getName())) {
            return false;
        }

        //controllo se il giocatore ha abbastanza fondi per acquistare la carta
        if (profile.spendFragments(card.getPrice())) {
            //sblocco la carta nel profilo
            profile.unlockCard(card.getName());
            return true;
        }

        return false;
    }
}