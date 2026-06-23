package it.unicam.cs.mpgc.rpg.matricola.domain;

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
public class BossAI {

    private final RandomGenerator random = RandomGenerator.getDefault();
    private final EnemyArchetype archetype;

    @FunctionalInterface
    private interface BossActionStrategy {
        BossMove execute(Character boss, Character player, RandomGenerator random);
    }

    private record WeightedAction(int weight, BossActionStrategy strategy, EnemyCard sourceCard) {}

    private final List<WeightedAction> actionPool = new ArrayList<>();

    public BossAI(EnemyArchetype archetype) {
        this.archetype = archetype;
        initializeActionPool();
    }

    /**
     * Restituisce le carte nemico dell'archetipo (per la visualizzazione del deck).
     */
    public List<EnemyCard> getCardPool() {
        return archetype.getCardPool();
    }

    public EnemyArchetype getArchetype() {
        return archetype;
    }

    /**
     * Restituisce le carte nemiche rimescolate (per il deck visivo).
     */
    public List<EnemyCard> getShuffledDeck() {
        List<EnemyCard> deck = new ArrayList<>(archetype.getCardPool());
        Collections.shuffle(deck);
        return deck;
    }

    private void initializeActionPool() {
        List<EnemyCard> cards = archetype.getCardPool();

        // Distribuzione pesi: 35, 25, 20, 15, 5
        int[] weights = {35, 25, 20, 15, 5};

        for (int i = 0; i < cards.size() && i < weights.length; i++) {
            final EnemyCard card = cards.get(i);
            final int weight = weights[i];

            BossActionStrategy strategy = createStrategy(card);
            actionPool.add(new WeightedAction(weight, strategy, card));
        }
    }

    /**
     * Crea la strategia di esecuzione basandosi solo sul tipo e sui valori della carta.
     * Nessun switch per archetipo: i valori sono parametrizzati nella EnemyCard.
     */
    private BossActionStrategy createStrategy(EnemyCard card) {
        return switch (card.type()) {
            case ATTACK -> (boss, player, rnd) -> {
                player.takeDamage(card.primaryValue());
                return new BossMove(card.name(),
                        card.description().replace("danni casuali", card.primaryValue() + " danni"),
                        card.imagePath(), card.type());
            };
            case DRAIN -> (boss, player, rnd) -> {
                player.takeDamage(card.primaryValue());
                boss.heal(card.secondaryValue());
                return new BossMove(card.name(), card.description(), card.imagePath(), card.type());
            };
            case DEBUFF -> (boss, player, rnd) -> {
                int focusLost = Math.min(card.primaryValue(), player.getCurrentFocus());
                if (focusLost > 0) {
                    player.consumeFocus(focusLost);
                    return new BossMove(card.name(),
                            card.description().replaceAll("\\d+ Focus", focusLost + " Focus"),
                            card.imagePath(), card.type());
                } else {
                    player.takeDamage(card.secondaryValue());
                    return new BossMove(card.name(),
                            "Mente vuota... L'urto si riversa sul corpo: " + card.secondaryValue() + " danni!",
                            card.imagePath(), EnemyCardType.ATTACK);
                }
            };
            case SPECIAL -> (boss, player, rnd) -> {
                int damage;
                if (card.secondaryValue() > 0) {
                    damage = rnd.nextInt(card.secondaryValue() - card.primaryValue() + 1) + card.primaryValue();
                } else {
                    damage = card.primaryValue();
                }
                player.takeDamage(damage);
                return new BossMove(card.name(),
                        card.description().replace("danni casuali", damage + " danni"),
                        card.imagePath(), card.type());
            };
        };
    }

    public BossMove takeTurn(Character boss, Character player) {
        int totalWeight = actionPool.stream().mapToInt(WeightedAction::weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;

        for (WeightedAction action : actionPool) {
            cumulative += action.weight();
            if (roll < cumulative) {
                return action.strategy().execute(boss, player, random);
            }
        }

        return actionPool.get(0).strategy().execute(boss, player, random);
    }
}
