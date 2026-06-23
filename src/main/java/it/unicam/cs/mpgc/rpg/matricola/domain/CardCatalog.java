package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Funge da archivio per tutte le carte disponibili nel gioco.
 * Rispetta l'Open/Closed Principle: le nuove carte si aggiungono qui senza modificare la logica di gioco.
 */
public class CardCatalog {

    private final List<Card> availableCards;

    public CardCatalog() {
        Rollable d6 = new StandardDice(6, RandomGenerator.getDefault());

        // --- CARTE BASE (Gratuite, le avrà fin dall'inizio) ---
        Card risonanza = new Card("Risonanza Eterea", 1, d6,
                (source, target, roll) -> source.restoreFocus(roll.value()), "/images/risonanza_eterea.png");

        Card fenditura = new Card("Fenditura Quantica", 2, d6,
                (source, target, roll) -> target.takeDamage(3 + roll.value()), "/images/fenditura_quantica.png");

        Card sovraccarico = new Card("Sovraccarico Biologico", 4, d6,
                (source, target, roll) -> { target.takeDamage(roll.value() * 2); source.takeDamage(2); }, "/images/sovraccarico_biologico.png");

        Card innesto = new Card("Innesto Simbiotico", 3, d6,
                (source, target, roll) -> source.heal(roll.value() + 1), "/images/innesto_simbiotico.png");

        Card assimilazione = new Card("Assimilazione Oscura", 0, null,
                (source, target, roll) -> { source.takeDamage(3); source.restoreFocus(4); }, "/images/assimilazione_oscura.png");

        // --- CARTE PREMIUM (Da comprare nel Negozio - Prezzo indicato alla fine) ---
        Card scudo = new Card("Egida del Vuoto", 2, d6,
                (source, target, roll) -> source.heal(roll.value() + 3), null, 50); // Costa 50 Frammenti

        Card supernova = new Card("Supernova Tascabile", 5, d6,
                (source, target, roll) -> target.takeDamage(10 + (roll.value() * 2)), null, 100); // Costa 100 Frammenti

        this.availableCards = List.of(
                risonanza, fenditura, sovraccarico, innesto, assimilazione, scudo, supernova
        );
    }

    /**
     * @return Una copia non modificabile del catalogo completo.
     */
    public List<Card> getAllCards() {
        return List.copyOf(availableCards);
    }
}