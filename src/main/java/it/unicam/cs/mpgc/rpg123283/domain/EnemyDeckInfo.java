package it.unicam.cs.mpgc.rpg123283.domain;

import java.util.List;

//interfaccia per le informazioni di presentazione/query sul deck nemico
//separata da IEnemyAI (che gestisce solo la logica AI) per rispettare
//l'interface Segregation Principle
public interface EnemyDeckInfo {
    List<EnemyCard> getCardPool();
    List<EnemyCard> getShuffledDeck();
    EnemyArchetype getArchetype();
}
