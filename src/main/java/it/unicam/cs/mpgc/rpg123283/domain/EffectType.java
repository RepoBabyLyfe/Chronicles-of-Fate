package it.unicam.cs.mpgc.rpg123283.domain;

//tipi di effetti delle carte giocabili usando il pattern strategy
//permette di mappare le logiche complesse da un file JSON senza usare reflection o script
public enum EffectType {
    RESTORE_FOCUS {
        @Override
        public void execute(Character source, Targetable target, int diceValue, int baseValue, int diceMult, int selfDamage) {
            source.restoreFocus(baseValue + diceValue * diceMult);
        }
    },
    DAMAGE {
        @Override
        public void execute(Character source, Targetable target, int diceValue, int baseValue, int diceMult, int selfDamage) {
            target.takeDamage(baseValue + diceValue * diceMult);
        }
    },
    DAMAGE_AND_SELF_DAMAGE {
        @Override
        public void execute(Character source, Targetable target, int diceValue, int baseValue, int diceMult, int selfDamage) {
            target.takeDamage(baseValue + diceValue * diceMult);
            source.takeDamage(selfDamage);
        }
    },
    HEAL {
        @Override
        public void execute(Character source, Targetable target, int diceValue, int baseValue, int diceMult, int selfDamage) {
            source.heal(baseValue + diceValue * diceMult);
        }
    },
    SELF_DAMAGE_RESTORE_FOCUS {
        @Override
        public void execute(Character source, Targetable target, int diceValue, int baseValue, int diceMult, int selfDamage) {
            source.takeDamage(selfDamage);
            source.restoreFocus(baseValue + diceValue * diceMult);
        }
    };

    public abstract void execute(Character source, Targetable target, int diceValue, int baseValue, int diceMult, int selfDamage);
}
