package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.random.RandomGenerator;

public class EntropyAI {
    private final RandomGenerator random = RandomGenerator.getDefault();

    public BossMove takeTurn(Character boss, Character player) {
        int chance = random.nextInt(100);

        if (chance < 35) {
            player.takeDamage(6);
            return new BossMove("Lacerazione Spaziale", "Gli artigli dell'Entropia strappano la realtà: 6 danni all'Aura.", "/images/lacerazione.png");
        } else if (chance < 60) {
            player.takeDamage(3);
            boss.heal(3);
            return new BossMove("Miasma Parassitario", "Spore aliene ti infettano: perdi 3 Aura e il Boss si rigenera.", "/images/miasma.png");
        } else if (chance < 80) {
            int focusPerso = Math.min(2, player.getCurrentFocus());
            if (focusPerso > 0) {
                player.consumeFocus(focusPerso);
                return new BossMove("Intrusione Psionica", "Tentacoli mentali violano i tuoi pensieri: persi " + focusPerso + " Focus!", "/images/psionica.png");
            } else {
                player.takeDamage(4);
                return new BossMove("Urto Mentale", "Mente vuota... L'urto psionico si riversa sul tuo corpo: 4 danni!", "/images/urto_mentale.png");
            }
        } else if (chance < 95) {
            int dannoCaotico = random.nextInt(7) + 2;
            player.takeDamage(dannoCaotico);
            return new BossMove("Fluttuazione Quantica", "La materia cosmica impazzisce: subisci un'anomalia di " + dannoCaotico + " danni!", "/images/quantica.png");
        } else {
            player.takeDamage(12);
            return new BossMove("Collasso Supernova", "COLLASSO SUPERNOVA! Rilasciata pura materia oscura: 12 danni critici!", "/images/supernova.png");
        }
    }
}