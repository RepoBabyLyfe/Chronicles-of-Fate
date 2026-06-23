package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Intelligenza Artificiale dei Boss generalizzata.
 * Carica il pool di azioni dall'archetipo specificato e le esegue con pesi bilanciati.
 * Non contiene logica specifica per archetipo: tutti i parametri vengono letti dalla EnemyCard.
 * Aggiungere un nuovo archetipo non richiede modifiche a questa classe (Open/Closed Principle).
 */
public class BossAI implements IEnemyAI, EnemyDeckInfo {

    private final RandomGenerator random = RandomGenerator.getDefault();
    private final EnemyArchetype archetype;

    private record WeightedAction(int weight, EnemyCard card) {}

    private final List<WeightedAction> actionPool = new ArrayList<>();

    public BossAI(EnemyArchetype archetype) {
        this.archetype = archetype;
        initializeActionPool();
    }

    //restituisce le carte nemico dell'archetipo visualizzazione deck
    @Override
    public List<EnemyCard> getCardPool() {
        return archetype.getCardPool();
    }

    @Override
    public EnemyArchetype getArchetype() {
        return archetype;
    }

    //restituisce carte nemiche rimescolate per il deck visivo
    @Override
    public List<EnemyCard> getShuffledDeck() {
        List<EnemyCard> deck = new ArrayList<>(archetype.getCardPool());
        Collections.shuffle(deck);
        return deck;
    }

    private void initializeActionPool() {
        List<EnemyCard> cards = archetype.getCardPool();

        //distribuzione pesi base: 35, 25, 20, 15, 5
        int[] baseWeights = {35, 25, 20, 15, 5};

        for (int i = 0; i < cards.size(); i++) {
            final EnemyCard card = cards.get(i);
            //fallback dinamico se ci sono più di 5 carte per evitare magic numbers e indexOutOfBounds
            final int weight = i < baseWeights.length ? baseWeights[i] : 5; 
            actionPool.add(new WeightedAction(weight, card));
        }
    }

    @Override
    public BossMove takeTurn(Character boss, Character player) {
        if (actionPool.isEmpty()) {
            throw new IllegalStateException("L'archetipo non ha carte disponibili.");
        }

        int totalWeight = actionPool.stream().mapToInt(WeightedAction::weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;

        for (WeightedAction action : actionPool) {
            cumulative += action.weight();
            if (roll < cumulative) {
                return action.card().type().execute(action.card(), boss, player, random);
            }
        }

        EnemyCard fallbackCard = actionPool.get(0).card();
        return fallbackCard.type().execute(fallbackCard, boss, player, random);
    }
}
