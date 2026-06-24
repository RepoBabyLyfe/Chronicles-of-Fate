# Chronicles of Fate

**Chronicles of Fate** è un videogioco *Roguelike Deck-Builder* a turni sviluppato in **Java e JavaFX**. 
Immergiti in un'ambientazione *Cyber-SciFi*, vesti i panni del "Tessitore di Fati", combatti temibili Boss cosmici, raccogli frammenti di etere e arricchisci il tuo arsenale di carte nel Negozio Cosmico.

## Caratteristiche Principali
- **Sistema di Combattimento Ibrido:** Combina la rigorosa pianificazione strategica (gestione dei punti *Focus* e sinergie tra carte) con un elemento di aleatorietà bilanciata tramite il lancio di dadi (D6).
- **Intelligenza Artificiale (IA) Nemica:** I Boss reagiscono dinamicamente allo stato della plancia (es. lanciano incantesimi curativi se in pericolo di vita, o sferrano mosse di "Esecuzione" se il giocatore ha HP critici).
- **Deck Building & Progressione:** Sconfiggi i nemici per apprendere le loro mosse, spendi i Frammenti di Etere nello Shop, e costruisci il mazzo perfetto personalizzandolo prima di ogni scontro.
- **Salvataggi Mid-Combat:** Tutti i progressi, inclusi lo stato esatto del giocatore e del nemico nel bel mezzo di un combattimento, vengono serializzati e salvati in tempo reale su file `.json`.

## Requisiti di Sistema
- **Java Development Kit (JDK):** Versione **21** o superiore (obbligatorio per le corrette dipendenze di JavaFX 21).
- **Sistema di Build:** Gradle (il wrapper è incluso nel repository, non richiede installazione manuale).

## Istruzioni per l'Esecuzione

Il progetto utilizza **Gradle Wrapper** per la gestione autonoma delle dipendenze e dell'avvio. Apri il terminale nella cartella principale del progetto (dove si trova il file `build.gradle`) e lancia uno dei seguenti comandi in base al tuo sistema operativo:

**Su Windows:**
```powershell
gradlew.bat run
```

**Su Linux o macOS:**
```bash
./gradlew run
```

> **Documentazione Tecnica:** Per leggere la documentazione tecnica completa sull'architettura (Clean Architecture, Dependency Rule) e le approfondite implementazioni dei Design Pattern (Facade, Observer, State Pattern, ecc...), ti invitiamo a leggere l'ampio file **[WIKI.md](WIKI.md)** incluso in questo repository.

---

## Dichiarazione Utilizzo di Strumenti di AI
Si dichiara l'utilizzo mirato e documentato di strumenti di Intelligenza Artificiale (come LLM per il codice e motori Text-to-Image) durante l'intera realizzazione tecnica e artistica di questo progetto. 
L'AI è stata impiegata come supporto avanzato allo sviluppo per l'ingegnerizzazione dell'architettura (Refactoring, Dependency Injection), le ottimizzazioni del rendering (risoluzione di memory leak e implementazione di Image Cache per l'eliminazione dei lag), e per la generazione di tutti gli asset grafici (illustrazioni delle carte e UI), permettendo di ottenere uno stile artistico *Cyber-SciFi* coeso e professionale senza l'ausilio di un team artistico esterno.

Tutto il codice architetturale e le logiche generate sono state rigorosamente verificate e revisionate in modo da rispettare le severe norme ingegneristiche originali del progetto. I dettagli macroscopici sull'utilizzo dell'AI sono rendicontati e approfonditi in un capitolo dedicato all'interno del file **WIKI.md**.
