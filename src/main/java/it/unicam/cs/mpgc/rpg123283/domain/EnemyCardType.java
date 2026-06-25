package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.random.RandomGenerator;

/**
 * tipologia di carta nemico per differenziazione visiva e meccanica
 * implementa lo Strategy Pattern per la risoluzione dell'attacco
 */
public enum EnemyCardType {
    ATTACK {
        @Override
        public BossMove execute(EnemyCard card, Character boss, Character player, RandomGenerator rnd) {
            player.takeDamage(card.primaryValue());
            return new BossMove(card.name(),
                    card.description().replace("danni casuali", card.primaryValue() + " danni"),
                    card.imagePath(), card.type());
        }
    },
    DRAIN {
        @Override
        public BossMove execute(EnemyCard card, Character boss, Character player, RandomGenerator rnd) {
            player.takeDamage(card.primaryValue());
            boss.heal(card.secondaryValue());
            return new BossMove(card.name(), card.description(), card.imagePath(), card.type());
        }
    },
    DEBUFF {
        @Override
        public BossMove execute(EnemyCard card, Character boss, Character player, RandomGenerator rnd) {
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
        }
    },
    SPECIAL {
        @Override
        public BossMove execute(EnemyCard card, Character boss, Character player, RandomGenerator rnd) {
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
        }
    };

    public abstract BossMove execute(EnemyCard card, Character boss, Character player, RandomGenerator rnd);
}
