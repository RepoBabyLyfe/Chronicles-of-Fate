package it.unicam.cs.mpgc.rpg123283.domain;

/**
 * Contratto del dominio per la notifica di eventi di combattimento.
 * Permette al domain layer di comunicare verso l'esterno senza dipendere
 * dal layer application, rispettando la Layered Architecture.
 */
public interface CombatEventListener {

    /**
     * Notifica che il nemico ha giocato una carta.
     * @param cardName Nome della carta giocata.
     * @param logMessage Messaggio descrittivo dell'azione.
     * @param imagePath Percorso dell'immagine della carta.
     */
    void onEnemyCardPlayed(String cardName, String logMessage, String imagePath);

    /**
     * Notifica un messaggio di log del combattimento.
     * @param message Messaggio da registrare.
     */
    void onLogMessage(String message);

    /**
     * Notifica che un personaggio ha subito danno.
     * @param target Personaggio che ha subito il danno.
     * @param damageAmount Quantità di danno subito.
     * @param isCritical Indica se il danno è critico.
     */
    void onDamageTaken(Character target, int damageAmount, boolean isCritical);
}
