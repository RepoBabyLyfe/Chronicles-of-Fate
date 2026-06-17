package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.List;

/**
 * Implementazione concreta di CardEffect che infligge danni basati su fasce di risultati.
 * Sostituisce i blocchi switch-case garantendo il rispetto dell'Open-Closed Principle.
 */
public class TieredDamageEffect implements CardEffect {

    private final List<DamageTier> tiers;

    public TieredDamageEffect(List<DamageTier> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new IllegalArgumentException("Le fasce di danno non possono essere vuote.");
        }
        this.tiers = List.copyOf(tiers); // Copia difensiva per l'immutabilità
    }

    @Override
    public void apply(Targetable target, RollResult roll) {
        tiers.stream()
                .filter(tier -> tier.contains(roll.value()))
                .findFirst()
                .ifPresent(tier -> target.takeDamage(tier.damageAmount()));
    }
}