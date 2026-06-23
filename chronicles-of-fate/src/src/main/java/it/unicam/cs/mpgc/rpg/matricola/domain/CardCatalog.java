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
                (source, target, roll) -> source.heal(roll.value() + 3), "/images/egida_vuoto.png", 50); // Costa 50 Frammenti

        Card supernova = new Card("Supernova Tascabile", 5, d6,
                (source, target, roll) -> target.takeDamage(10 + (roll.value() * 2)), "/images/supernova_tascabile.png", 100); // Costa 100 Frammenti

        // --- CARTE BOSS ENTROPIA ---
        Card b0_1 = new Card("Lacerazione Spaziale", 2, d6, (s, t, r) -> t.takeDamage(6), "/images/lacerazione.png");
        Card b0_2 = new Card("Miasma Parassitario", 3, d6, (s, t, r) -> { t.takeDamage(3); s.heal(3); }, "/images/miasma.png");
        Card b0_3 = new Card("Intrusione Psionica", 1, d6, (s, t, r) -> s.restoreFocus(3), "/images/psionica.png"); // Adattata per dare Focus
        Card b0_4 = new Card("Fluttuazione Quantica", 3, d6, (s, t, r) -> t.takeDamage(r.value() + 2), "/images/quantica.png");
        Card b0_5 = new Card("Collasso Supernova", 5, d6, (s, t, r) -> t.takeDamage(12), "/images/supernova.png");

        // --- CARTE BOSS LEVIATANO ---
        Card b1_1 = new Card("Onda d'Urto Astrale", 2, d6, (s, t, r) -> t.takeDamage(6), "/images/onda_urto.png");
        Card b1_2 = new Card("Risucchio Abissale", 3, d6, (s, t, r) -> { t.takeDamage(4); s.heal(4); }, "/images/risucchio_abissale.png");
        Card b1_3 = new Card("Nebbia della Disperazione", 2, d6, (s, t, r) -> t.takeDamage(4), "/images/nebbia.png");
        Card b1_4 = new Card("Scaglie di Polvere di Stelle", 3, d6, (s, t, r) -> s.heal(6), "/images/scaglie.png");
        Card b1_5 = new Card("Ruggito del Creato", 4, d6, (s, t, r) -> t.takeDamage(10), "/images/ruggito.png");

        // --- CARTE BOSS ASSASSINO ---
        Card b2_1 = new Card("Lame di Plasma", 3, d6, (s, t, r) -> t.takeDamage(8), "/images/lame_plasma.png");
        Card b2_2 = new Card("Passo d'Ombra", 2, d6, (s, t, r) -> s.heal(3), "/images/passo_ombra.png");
        Card b2_3 = new Card("Veleno Etereo", 2, d6, (s, t, r) -> { t.takeDamage(2); s.heal(2); }, "/images/veleno_etereo.png");
        Card b2_4 = new Card("Fendente Quantico", 3, d6, (s, t, r) -> t.takeDamage(7), "/images/fendente.png");
        Card b2_5 = new Card("Esecuzione Stellare", 6, d6, (s, t, r) -> t.takeDamage(14), "/images/esecuzione.png");

        this.availableCards = List.of(
                risonanza, fenditura, sovraccarico, innesto, assimilazione, scudo, supernova,
                b0_1, b0_2, b0_3, b0_4, b0_5,
                b1_1, b1_2, b1_3, b1_4, b1_5,
                b2_1, b2_2, b2_3, b2_4, b2_5
        );
    }

    /**
     * @return Una copia non modificabile del catalogo completo.
     */
    public List<Card> getAllCards() {
        return List.copyOf(availableCards);
    }
}