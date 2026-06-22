package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.List;
import java.util.Map;

/**
 * Definisce gli archetipi dei boss disponibili nel gioco.
 * Ogni archetipo ha nome, HP, e il proprio set di carte nemico.
 */
public enum EnemyArchetype {

    ENTROPY_AVATAR(
            "Avatar dell'Entropia",
            5,
            List.of(
                    new EnemyCard("Lacerazione Spaziale",
                            "Gli artigli dell'Entropia strappano la realtà: 6 danni all'Aura.",
                            "/images/lacerazione.png", EnemyCardType.ATTACK),
                    new EnemyCard("Miasma Parassitario",
                            "Spore aliene ti infettano: perdi 3 Aura e il Boss si rigenera.",
                            "/images/miasma.png", EnemyCardType.DRAIN),
                    new EnemyCard("Intrusione Psionica",
                            "Tentacoli mentali violano i tuoi pensieri: perdi 2 Focus!",
                            "/images/psionica.png", EnemyCardType.DEBUFF),
                    new EnemyCard("Fluttuazione Quantica",
                            "La materia cosmica impazzisce: subisci un'anomalia di danni casuali!",
                            "/images/quantica.png", EnemyCardType.SPECIAL),
                    new EnemyCard("Collasso Supernova",
                            "COLLASSO SUPERNOVA! Rilasciata pura materia oscura: 12 danni critici!",
                            "/images/supernova.png", EnemyCardType.SPECIAL)
            )
    ),

    VOID_SENTINEL(
            "Sentinella del Vuoto",
            65,
            List.of(
                    new EnemyCard("Raggio Annientatore",
                            "Un raggio concentrato perfora le difese: 8 danni puri.",
                            "/images/raggio_annientatore.png", EnemyCardType.ATTACK),
                    new EnemyCard("Barriera Entropica",
                            "La Sentinella assorbe energia: si cura di 5 Aura.",
                            "/images/barriera_entropica.png", EnemyCardType.DRAIN),
                    new EnemyCard("Distorsione Gravitazionale",
                            "La gravità si inverte: perdi 3 Focus e subisci 2 danni.",
                            "/images/distorsione_grav.png", EnemyCardType.DEBUFF),
                    new EnemyCard("Implosione del Vuoto",
                            "Il vuoto collassa su se stesso: 4 danni e il Boss si cura di 2.",
                            "/images/implosione.png", EnemyCardType.DRAIN),
                    new EnemyCard("Singolarità Cosmica",
                            "SINGOLARITÀ! Un buco nero temporaneo: 15 danni devastanti!",
                            "/images/singolarita.png", EnemyCardType.SPECIAL)
            )
    ),

    CHRONO_DEVOURER(
            "Divoratore Cronico",
            40,
            List.of(
                    new EnemyCard("Morso Temporale",
                            "Le fauci del tempo strappano frammenti di realtà: 5 danni.",
                            "/images/morso_temporale.png", EnemyCardType.ATTACK),
                    new EnemyCard("Risucchio Cronologico",
                            "Il tempo si riavvolge per il nemico: il Boss si cura di 4 Aura.",
                            "/images/risucchio_crono.png", EnemyCardType.DRAIN),
                    new EnemyCard("Paradosso Causale",
                            "Un paradosso temporale: perdi 4 Focus!",
                            "/images/paradosso.png", EnemyCardType.DEBUFF),
                    new EnemyCard("Accelerazione Entropica",
                            "Il tempo accelera: 3 danni rapidi seguiti da altri 3 danni.",
                            "/images/accelerazione.png", EnemyCardType.ATTACK),
                    new EnemyCard("Annullamento Temporale",
                            "IL TEMPO SI FERMA! Devastazione pura: 10 danni!",
                            "/images/annullamento.png", EnemyCardType.SPECIAL)
            )
    );

    private final String bossName;
    private final int baseHp;
    private final List<EnemyCard> cardPool;

    EnemyArchetype(String bossName, int baseHp, List<EnemyCard> cardPool) {
        this.bossName = bossName;
        this.baseHp = baseHp;
        this.cardPool = cardPool;
    }

    public String getBossName() { return bossName; }
    public int getBaseHp() { return baseHp; }
    public List<EnemyCard> getCardPool() { return cardPool; }
}
