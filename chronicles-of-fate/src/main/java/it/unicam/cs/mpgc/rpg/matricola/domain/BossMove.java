package it.unicam.cs.mpgc.rpg.matricola.domain;

/**
 * Rappresenta la mossa scelta dall'Intelligenza Artificiale.
 * Dà struttura alle azioni del Boss invece di usare semplici Stringhe.
 */
public record BossMove(String name, String logMessage, String imagePath) {}