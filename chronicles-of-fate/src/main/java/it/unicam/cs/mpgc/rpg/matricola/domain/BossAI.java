package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Intelligenza Artificiale dei Boss generalizzata.
 * Carica il pool di azioni dall'archetipo specificato e le esegue con pesi bilanciati.
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

            BossActionStrategy strategy = createStrategy(card, archetype);
            actionPool.add(new WeightedAction(weight, strategy, card));
        }
    }

    private BossActionStrategy createStrategy(EnemyCard card, EnemyArchetype archetype) {
        return switch (card.type()) {
            case ATTACK -> (boss, player, rnd) -> {
                int damage = calculateAttackDamage(card, archetype, rnd);
                player.takeDamage(damage);
                return new BossMove(card.name(),
                        card.description().replace("danni casuali", damage + " danni"),
                        card.imagePath(), card.type());
            };
            case DRAIN -> (boss, player, rnd) -> {
                int[] values = calculateDrainValues(card, archetype, rnd);
                player.takeDamage(values[0]);
                boss.heal(values[1]);
                return new BossMove(card.name(), card.description(), card.imagePath(), card.type());
            };
            case DEBUFF -> (boss, player, rnd) -> {
                int focusLost = Math.min(calculateDebuffValue(card, archetype), player.getCurrentFocus());
                if (focusLost > 0) {
                    player.consumeFocus(focusLost);
                    return new BossMove(card.name(),
                            card.description().replace("2 Focus", focusLost + " Focus")
                                    .replace("3 Focus", focusLost + " Focus")
                                    .replace("4 Focus", focusLost + " Focus"),
                            card.imagePath(), card.type());
                } else {
                    player.takeDamage(4);
                    return new BossMove(card.name(),
                            "Mente vuota... L'urto si riversa sul corpo: 4 danni!",
                            card.imagePath(), EnemyCardType.ATTACK);
                }
            };
            case SPECIAL -> (boss, player, rnd) -> {
                int damage = calculateSpecialDamage(card, archetype, rnd);
                player.takeDamage(damage);
                return new BossMove(card.name(),
                        card.description().replace("danni casuali", damage + " danni"),
                        card.imagePath(), card.type());
            };
        };
    }

    private int calculateAttackDamage(EnemyCard card, EnemyArchetype archetype, RandomGenerator rnd) {
        return switch (archetype) {
            case ENTROPY_AVATAR -> 6;
            case VOID_SENTINEL -> card.name().contains("Raggio") ? 8 : 4;
            case CHRONO_DEVOURER -> card.name().contains("Accelerazione") ? 6 : 5;
        };
    }

    private int[] calculateDrainValues(EnemyCard card, EnemyArchetype archetype, RandomGenerator rnd) {
        return switch (archetype) {
            case ENTROPY_AVATAR -> new int[]{3, 3};
            case VOID_SENTINEL -> card.name().contains("Barriera") ? new int[]{0, 5} : new int[]{4, 2};
            case CHRONO_DEVOURER -> new int[]{0, 4};
        };
    }

    private int calculateDebuffValue(EnemyCard card, EnemyArchetype archetype) {
        return switch (archetype) {
            case ENTROPY_AVATAR -> 2;
            case VOID_SENTINEL -> 3;
            case CHRONO_DEVOURER -> 4;
        };
    }

    private int calculateSpecialDamage(EnemyCard card, EnemyArchetype archetype, RandomGenerator rnd) {
        return switch (archetype) {
            case ENTROPY_AVATAR -> card.name().contains("Collasso") ? 12 : rnd.nextInt(7) + 2;
            case VOID_SENTINEL -> 15;
            case CHRONO_DEVOURER -> 10;
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
