package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.List;

//definisce gli archetipi dei boss disponibili nel gioco.
//ogn i archetipo ha nome, HP, e il proprio set di carte nemico con valori parametrizzati.
//aggiungere un nuovo archetipo non richiede modifiche al BossAI (open/closed Principle).
public enum EnemyArchetype {

    ENTROPY_AVATAR(
            "Avatar dell'Entropia",
            50,
            List.of(
                    new EnemyCard("Lacerazione Spaziale",
                            "Gli artigli dell'Entropia strappano la realtà: 6 danni all'Aura.",
                            "/images/lacerazione.png", EnemyCardType.ATTACK, 6, 0),
                    new EnemyCard("Miasma Parassitario",
                            "Spore aliene ti infettano: perdi 3 Aura e il Boss si rigenera.",
                            "/images/miasma.png", EnemyCardType.DRAIN, 3, 3),
                    new EnemyCard("Intrusione Psionica",
                            "Tentacoli mentali violano i tuoi pensieri: perdi 2 Focus!",
                            "/images/psionica.png", EnemyCardType.DEBUFF, 2, 4),
                    new EnemyCard("Fluttuazione Quantica",
                            "La materia cosmica impazzisce: subisci un'anomalia di danni casuali!",
                            "/images/quantica.png", EnemyCardType.SPECIAL, 2, 8),
                    new EnemyCard("Collasso Supernova",
                            "COLLASSO SUPERNOVA! Rilasciata pura materia oscura: 12 danni critici!",
                            "/images/supernova.png", EnemyCardType.SPECIAL, 12, 0)
            )
    ),

    COSMIC_LEVIATHAN(
            "Leviatano Cosmico",
            80,
            List.of(
                    new EnemyCard("Onda d'Urto Astrale",
                            "Una massiccia perturbazione cosmica: 6 danni.",
                            "/images/onda_urto.png", EnemyCardType.ATTACK, 6, 0),
                    new EnemyCard("Risucchio Abissale",
                            "Fauci insaziabili: 4 danni e il Boss si cura di 4 HP.",
                            "/images/risucchio_abissale.png", EnemyCardType.DRAIN, 4, 4),
                    new EnemyCard("Nebbia della Disperazione",
                            "Un terrore ancestrale avvolge la tua mente: perdi 4 Focus.",
                            "/images/nebbia.png", EnemyCardType.DEBUFF, 4, 4),
                    new EnemyCard("Scaglie di Polvere di Stelle",
                            "Il Leviatano rigenera i tessuti danneggiati: si cura di 6 HP.",
                            "/images/scaglie.png", EnemyCardType.DRAIN, 0, 6),
                    new EnemyCard("Ruggito del Creato",
                            "L'intero cosmo trema per il ruggito: 10 danni critici!",
                            "/images/ruggito.png", EnemyCardType.SPECIAL, 10, 0)
            )
    ),

    NEBULA_ASSASSIN(
            "Assassino della Nebulosa",
            35,
            List.of(
                    new EnemyCard("Lame di Plasma",
                            "Assalti veloci e penetranti: 8 danni perforanti.",
                            "/images/lame_plasma.png", EnemyCardType.ATTACK, 8, 0),
                    new EnemyCard("Passo d'Ombra",
                            "Movimenti impercettibili curano il corpo: si cura di 3 HP.",
                            "/images/passo_ombra.png", EnemyCardType.DRAIN, 0, 3),
                    new EnemyCard("Veleno Etereo",
                            "Sostanze tossiche nel flusso vitale: perdi 2 Focus e subisci 2 danni.",
                            "/images/veleno_etereo.png", EnemyCardType.DEBUFF, 2, 2),
                    new EnemyCard("Fendente Quantico",
                            "Un colpo che ignora la distanza: 7 danni.",
                            "/images/fendente.png", EnemyCardType.ATTACK, 7, 0),
                    new EnemyCard("Esecuzione Stellare",
                            "Un colpo letale mirato ai punti deboli: 14 danni critici!",
                            "/images/esecuzione.png", EnemyCardType.SPECIAL, 14, 0)
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
