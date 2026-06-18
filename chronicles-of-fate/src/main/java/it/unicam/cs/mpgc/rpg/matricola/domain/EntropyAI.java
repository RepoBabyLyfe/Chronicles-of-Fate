package it.unicam.cs.mpgc.rpg.matricola.domain;

import java.util.random.RandomGenerator;

/**
 * Intelligenza Artificiale dell'Avatar dell'Entropia.
 * Implementa 5 mosse a tema alieno/cosmico con pesi di probabilità differenziati.
 */
public class EntropyAI {
    private final RandomGenerator random = RandomGenerator.getDefault();

    public String takeTurn(Character boss, Character player) {
        // Estraiamo un numero da 0 a 99 per gestire le percentuali (0-100%)
        int chance = random.nextInt(100);

        if (chance < 35) {
            // 0-34 (35%) -> Lacerazione Spaziale
            player.takeDamage(6);
            return "Lacerazione Spaziale! Gli artigli dell'Entropia strappano la realtà: 6 danni all'Aura.";

        } else if (chance < 60) {
            // 35-59 (25%) -> Miasma Parassitario
            player.takeDamage(3);
            boss.heal(3);
            return "Miasma Parassitario! Spore aliene ti infettano: perdi 3 Aura e il Boss si rigenera.";

        } else if (chance < 80) {
            // 60-79 (20%) -> Intrusione Psionica
            int focusPerso = Math.min(2, player.getCurrentFocus());

            if (focusPerso > 0) {
                player.consumeFocus(focusPerso);
                return "Intrusione Psionica! Tentacoli mentali violano i tuoi pensieri: persi " + focusPerso + " Focus!";
            } else {
                // Seleziona un bersaglio fisico se la mente è già vuota
                player.takeDamage(4);
                return "Mente vuota... L'urto psionico fallisce e si riversa sul tuo corpo: 4 danni!";
            }

        } else if (chance < 95) {
            // 80-94 (15%) -> Fluttuazione Quantica (Danno random da 2 a 8)
            int dannoCaotico = random.nextInt(7) + 2;
            player.takeDamage(dannoCaotico);
            return "Fluttuazione Quantica! La materia cosmica impazzisce: subisci un'anomalia di " + dannoCaotico + " danni!";

        } else {
            // 95-99 (5%) -> Collasso Supernova (Mossa Suprema)
            player.takeDamage(12);
            return "COLLASSO SUPERNOVA! L'Avatar rilascia pura materia oscura: 12 danni critici all'Aura!";
        }
    }
}