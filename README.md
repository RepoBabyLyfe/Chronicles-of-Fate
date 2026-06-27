# Chronicles of Fate


**Chronicles of Fate** è un progetto sviluppato in **Java** e **JavaFX** che implementa un videogioco *Roguelike Deck-Builder* a turni. Il gameplay combina costruzione dinamica del mazzo, combattimenti strategici contro Boss e gestione delle risorse in un'ambientazione **Cyber-SciFi**, dove il giocatore assume il ruolo del **Tessitore di Fati**.

## Caratteristiche Principali
- **Sistema di Combattimento Ibrido:** Viene combinata la pianificazione strategica (gestione dei punti *Focus* e sinergie tra carte) con un elemento di aleatorietà bilanciata tramite il lancio di dadi (D6).
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

> **Documentazione tecnica:** il file **[WIKI.md](WIKI.md)** raccoglie la documentazione completa del progetto, incluse l'architettura software, i design pattern utilizzati e le principali scelte progettuali.

---

## Dichiarazione Utilizzo di Strumenti di AI
Si dichiara l'utilizzo mirato e documentato di strumenti di Intelligenza Artificiale ( LLM per il codice e motori Text-to-Image) durante l'intera realizzazione tecnica e artistica di questo progetto. 
L'AI è stata impiegata come supporto avanzato allo sviluppo per l'ingegnerizzazione dell'architettura (Refactoring, Dependency Injection), le ottimizzazioni del rendering (risoluzione di memory leak e implementazione di Image Cache per l'eliminazione dei lag), e per la generazione di tutti gli asset grafici (illustrazioni delle carte e UI), permettendo di ottenere uno stile artistico *Cyber-SciFi* coeso e professionale senza l'ausilio di un team artistico esterno.

Il codice e le logiche generate sono stati verificati e revisionati per garantirne la coerenza con l'architettura e gli standard progettuali adottati. Maggiori dettagli sull'utilizzo dell'AI sono disponibili nel file **WIKI.md**.

