package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.List;

public class TieredDamageEffect implements CardEffect {

    private final List<DamageTier> tiers;

    public TieredDamageEffect(List<DamageTier> tiers) {
        if (tiers == null || tiers.isEmpty()) {
            throw new IllegalArgumentException("Le fasce di danno non possono essere vuote.");
        }
        this.tiers = List.copyOf(tiers);
    }

    @Override
    public void apply(Character source, Targetable target, RollResult roll) {
        tiers.stream()
                .filter(tier -> tier.contains(roll.value()))
                .findFirst()
                .ifPresent(tier -> target.takeDamage(tier.damageAmount()));
    }
}