package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class EntropyAI {
    private final RandomGenerator random = RandomGenerator.getDefault();

    @FunctionalInterface
    private interface BossActionStrategy {
        BossMove execute(Character boss, Character player, RandomGenerator random);
    }

    private record WeightedAction(int weight, BossActionStrategy strategy) {}

    private final List<WeightedAction> actionPool = new ArrayList<>();

    public EntropyAI() {

        //35% di probabilità
        actionPool.add(new WeightedAction(35, (boss, player, rnd) -> {
            player.takeDamage(6);
            return new BossMove("Lacerazione Spaziale", "Gli artigli dell'Entropia strappano la realtà: 6 danni all'Aura.", "/images/lacerazione.png");
        }));

        //25% di probabilità
        actionPool.add(new WeightedAction(25, (boss, player, rnd) -> {
            player.takeDamage(3);
            boss.heal(3);
            return new BossMove("Miasma Parassitario", "Spore aliene ti infettano: perdi 3 Aura e il Boss si rigenera.", "/images/miasma.png");
        }));

        //20% di probabilità
        actionPool.add(new WeightedAction(20, (boss, player, rnd) -> {
            int focusPerso = Math.min(2, player.getCurrentFocus());
            if (focusPerso > 0) {
                player.consumeFocus(focusPerso);
                return new BossMove("Intrusione Psionica", "Tentacoli mentali violano i tuoi pensieri: persi " + focusPerso + " Focus!", "/images/psionica.png");
            } else {
                player.takeDamage(4);
                return new BossMove("Urto Mentale", "Mente vuota... L'urto psionico si riversa sul tuo corpo: 4 danni!", "/images/urto_mentale.png");
            }
        }));

        //15% di probabilità
        actionPool.add(new WeightedAction(15, (boss, player, rnd) -> {
            int dannoCaotico = rnd.nextInt(7) + 2;
            player.takeDamage(dannoCaotico);
            return new BossMove("Fluttuazione Quantica", "La materia cosmica impazzisce: subisci un'anomalia di " + dannoCaotico + " danni!", "/images/quantica.png");
        }));

        //5% di probabilità
        actionPool.add(new WeightedAction(5, (boss, player, rnd) -> {
            player.takeDamage(12);
            return new BossMove("Collasso Supernova", "COLLASSO SUPERNOVA! Rilasciata pura materia oscura: 12 danni critici!", "/images/supernova.png");
        }));
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

        // Fallback di sicurezza
        return actionPool.get(0).strategy().execute(boss, player, random);
    }
}