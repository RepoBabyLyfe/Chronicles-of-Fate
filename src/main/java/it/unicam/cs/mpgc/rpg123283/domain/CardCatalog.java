package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.List;

/**
 * interfaccia del dominio per l'accesso al catalogo delle carte
 * Le implementazioni concrete (es. da JSON) risiedono nel layer di persistenza
 */
public interface CardCatalog {

    /**
     * @return Una lista non modificabile di tutte le carte disponibili nel gioco.
     */
    List<Card> getAllCards();
}
