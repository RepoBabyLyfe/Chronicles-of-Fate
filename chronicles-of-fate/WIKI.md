# Chronicles of Fate - Documentazione Architetturale e Tecnica Completa

Il presente documento illustra l'architettura software, i Design Pattern adottati, la suddivisione delle responsabilità tra le componenti e i flussi di interazione all'interno del progetto **Chronicles of Fate**.

---

## 1. Architettura di Sistema

L'intero progetto è stato modellato applicando i principi della **Clean Architecture** e della **Dependency Rule**. Il sistema è diviso in strati concentrici, dove le dipendenze puntano *esclusivamente* verso l'interno (dal livello più esterno dell'interfaccia utente fino al nucleo puro delle regole di business).

> [!IMPORTANT]
> Il rispetto della Dependency Rule è un requisito architetturale inderogabile. Qualsiasi violazione come l'importazione di classi grafiche all'interno del layer Domain comprometterebbe l'intero paradigma progettuale.

### I 5 Livelli Architetturali
Il sistema si compone di cinque livelli fondamentali, ciascuno con responsabilità ben definite:

**Domain (`domain`)**
Rappresenta il nucleo del software. Non è interessato a nulla di esterno. Contiene le entità pure, la matematica del gioco e la State Machine del combattimento.

**Application (`application`)**
Rappresenta il livello dei Casi d'Uso. Definisce le interfacce (contratti) per i repository esterni e orchestra le interazioni tra gli oggetti di dominio (ad esempio, tramite il `GameService`).

**Presentation (`presentation`)**
Rappresenta l'interfaccia utente in JavaFX. Dipende da Application e Domain. È l'unico strato a conoscere l'esistenza di bottoni, schermi e animazioni.

**Persistence (`persistence`)**
Rappresenta lo strato tecnico che implementa i contratti di persistenza (salvataggio/caricamento). Dipende da Application. Utilizza librerie esterne come *Gson* per leggere/scrivere file.

**Infrastructure (`infrastructure`)**
Rappresenta il collante del sistema. Risiede nel livello più esterno e si occupa di istanziare e collegare concretamente tutti i livelli precedenti.

---

## 2. Design Pattern Utilizzati e Motivazioni

L'ingegneria del software di questo gioco fa uso di Design Pattern per risolvere problemi di accoppiamento e manutenibilità.

### 2.1 State Pattern
- **Dove si trova:** Nel `CombatManager` e nelle implementazioni di `TurnState` (`StartPhaseState`, `ActionPhaseState`, `EndPhaseState`).
- **Perché è stato usato:** Invece di avere un gigantesco e illeggibile blocco `if-else` o `switch` dentro `CombatManager` per determinare le azioni consentite (es. *se è il mio turno e sono nella fase di attacco allora...*), la logica è delegata a classi stato indipendenti. Se il giocatore tenta di giocare una carta nella `StartPhaseState`, lo Stato semplicemente rigetta l'azione o solleva un'eccezione, garantendo un flusso pulito e non prono a bug.

### 2.2 Observer & Adapter Pattern
- **Dove si trova:** Nell'infrastruttura di comunicazione `GameEventBus`, `EventPublisher`, `GameEventListener` e nell'adattatore `CombatEventListener` dentro il `GameService`.
- **Perché è stato usato:** Per disaccoppiare completamente la logica visiva (UI) da quella logica. Quando il `CombatManager` rileva la perdita di HP, non aggiorna direttamente la barra della vita (il Dominio non comunica direttamente con i componenti grafici). Al contrario, il Dominio comunica con una sua interfaccia passiva (`CombatEventListener`), che il `GameService` **adatta** (Adapter Pattern) emettendo un `HpChangedEvent` sull'Event Bus. I controller grafici, registrati come ascoltatori, intercettano l'evento e avviano le relative animazioni.

### 2.3 Facade Pattern
- **Dove si trova:** Nella classe `GameService`.
- **Perché è stato usato:** Il layer Presentation (i bottoni e le schermate) ha bisogno di interagire con il gioco, ma interfacciarsi singolarmente con `Shop`, `CombatManager`, `PlayerProfile`, `Deck` diventerebbe caotico. Il `GameService` fornisce un'interfaccia unificata ad alto livello (es. `playCard(card)`, `buyCard(card)`).

### 2.4 Dependency Injection (Constructor Injection)
- **Dove si trova:** In tutta l'applicazione, e in particolar modo implementata tramite il `controllerFactory` nel `SceneManager`.
- **Perché è stato usato:** Elimina l'uso di Singleton globali e metodi statici (anti-pattern) che rendevano il codice difficilmente testabile e accoppiato. Ogni Controller riceve le dipendenze vitali (il `GameService` e il `GameEventBus`) direttamente nel suo costruttore al momento del caricamento del file FXML.

### 2.5 Repository Pattern
- **Dove si trova:** In `GameStateRepository` (interfaccia) e `JsonGameStateRepository` (implementazione).
- **Perché è stato usato:** Isola il dominio dalla tecnologia di salvataggio. L'applicazione dice semplicemente "Salva lo stato"; non sa e non le interessa se viene salvato su file di testo, JSON, database SQL o cloud.

### 2.6 Strategy Pattern
- **Dove si trova:** Nell'uso dell'interfaccia `Rollable` (implementata da `StandardDice`) e nell'astrazione `Targetable`.
- **Perché è stato usato:** Permette di incapsulare l'algoritmo (es. la logica di calcolo del lancio dei dadi o il bersaglio di un attacco) in classi interscambiabili a runtime. Cambiare il tipo di dado o la logica di bersagliamento non richiede di alterare la classe `CombatManager`.

### 2.7 Factory Method
- **Dove si trova:** Nella classe `GameFactory`.
- **Perché è stato usato:** Per delegare e centralizzare la complessa logica di istanziazione dei nemici. In base al parametro `defeatedBosses`, la Factory decide quale archetipo di Boss istanziare, liberando il `GameService` da pesanti switch-case di configurazione.

### 2.8 Principi S.O.L.I.D.
La robustezza del sistema è garantita anche dalla stretta aderenza ai principi SOLID:

**Single Responsibility Principle (SRP):** Per evitare che la classe `GameService` si trasformasse in un anti-pattern (God Class), le sue responsabilità operative sono state divise in due moduli focalizzati: il `CombatOrchestrator` per la gestione delle battaglie e il `ProfileManager` per la gestione dell'inventario. Il `GameService` rimane così un puro Facade.

**Open/Closed Principle (OCP):** Il sistema è progettato per essere aperto all'estensione ma chiuso alle modifiche. Nuovi elementi, come tipologie di carte, nemici o persino fasi del turno, possono essere introdotti estendendo le configurazioni esterne o implementando nuove interfacce base, senza mai intaccare il codice core esistente.

**Liskov Substitution Principle (LSP):** Qualsiasi implementazione concreta di un'astrazione può essere sostituita senza alterare la correttezza del programma. Il `CombatManager`, ad esempio, accetta qualsiasi istanza di `TurnState` o `Rollable`, garantendo la medesima coerenza matematica anche con dadi non standard.

**Interface Segregation Principle (ISP):** La monolitica interfaccia `IEnemyAI` costringeva le classi a dipendere sia dalla logica comportamentale sia dalla lettura passiva del mazzo nemico. È stata perciò suddivisa in due interfacce minimali e specifiche: `IEnemyAI` per l'intelligenza tattica attiva ed `EnemyDeckInfo` per l'interrogazione dei dati.

**Dependency Inversion Principle (DIP):** L'accesso al catalogo carte dipendeva inizialmente dall'istanziazione diretta di `JsonCardCatalog` sparpagliata nei vari Controller. Ora il livello domain espone un'astratta interfaccia `CardCatalog`, mentre la sua implementazione concreta risiede nel livello persistence e viene iniettata tramite costruttore, disaccoppiando totalmente il codice applicativo dalla tecnologia JSON.

---

## 3. Dettaglio delle Classi: Responsabilità e Comunicazione

Analizziamo ora il ruolo di ogni classe, come comunica e di chi è responsabile.

### 3.1 Layer `domain`

**`Character`**
Rappresenta l'entità base di gioco. Custodisce lo stato vitale (AURA/HP) del giocatore o dei Boss nemici. Include inoltre le regole di validazione per impedire che gli HP scendano sotto lo zero o superino il limite massimo consentito. La sua comunicazione è puramente passiva: si limita a ricevere comandi dal `CombatManager`, come ad esempio l'assegnazione dei danni (come `takeDamage(int)`).

**`CardCatalog` (Interfaccia)**
Costituisce l'applicazione diretta del Dependency Inversion Principle. Definisce il contratto puramente astratto per il recupero della collezione delle carte, blindando il Dominio e l'Application dai dettagli infrastrutturali di come queste informazioni vengano effettivamente reperite.

**`Card` e `CardEffect`**
Queste classi incarnano un approccio Data-Driven. Invece di creare decine di sottoclassi diverse per ogni singola carta, esiste un'unica classe `Card` popolata dinamicamente tramite JSON. Il suo comportamento specifico è dettato dall'enumeratore `EffectType` (come DAMAGE, HEAL, ecc.), elaborato a runtime. Sono entità passive manipolate dal `Deck`, lette dallo `Shop` o processate dal motore di combattimento.

- **`Rollable`, `StandardDice` e `RollResult`**
Rappresentano l'astrazione del lancio dei dadi. Anziché cablare `Math.random()` dentro il `CombatManager`, si utilizza l'interfaccia `Rollable`. `StandardDice` lancia un dado D6 fisico virtuale. Questo permette altissima testabilità (è possibile iniettare un dado "truccato" nei test) ed estensibilità (aggiungere dadi D8 o D20 in futuro).

**`EnemyArchetype` (Enum)**
Funge da catalogo centralizzato per i Boss. Definisce le statistiche base e i nomi dei tre incontri principali: l'Avatar dell'Entropia (Livello 0), il Leviatano Cosmico (Livello 1) e l'Assassino della Nebulosa (Livello 2).

- **`Deck`**
Implementa le meccaniche di manipolazione del mazzo. Gestisce una pila di pesca, una pila degli scarti e la logica di mescolamento casuale (`shuffle()`). Quando il mazzo è vuoto, ricicla gli scarti rimescolandoli. Viene posseduto direttamente dal `PlayerProfile` o generato al volo dal `CombatManager` all'inizio di ogni scontro.

**`CombatManager`**
L'entità centrale di orchestrazione della battaglia. Sebbene le sue molteplici responsabilità potrebbero spingerlo a diventare una God Class, il suo design delega in realtà gran parte del carico elaborativo. Mantiene lo stato ad alto livello, tenendo traccia di giocatore e nemico, ma affida l'esecuzione delle logiche specifiche (come l'avanzamento dei turni) ad altre componenti. Comunica invocando metodi sulle entità ed emettendo notifiche tramite l'`EventPublisher`.

**Fasi del Combattimento (`TurnState` e implementazioni)**
Rappresentano l'applicazione del Pattern State. La `StartPhaseState` rigenera il Focus e forza la pescata della mano iniziale. L'`ActionPhaseState` accetta i comandi dall'utente e ne risolve immediatamente gli effetti, calcoli dei dadi inclusi. Infine, l'`EndPhaseState` è la fase in cui l'IA nemica prende il controllo per eseguire la mossa di risposta. Esse comunicano ricevendo l'istanza del `CombatManager` per invocarne i relativi cambi di stato.

- **`BossAI` e `BossMove` (implementa `IEnemyAI` ed `EnemyDeckInfo`)**
La loro responsabilità è analizzare e reagire al contesto tattico in modo "intelligente" senza input umano. Sceglie l'azione ottimale dal deck nemico valutando la percentuale di HP residui del boss o la minaccia imminente. L'applicazione dell'*ISP* (Interface Segregation Principle) impone che chi vuole sapere le mosse del boss interroghi `EnemyDeckInfo`, chi vuole fargli fare una mossa chiami `IEnemyAI`. Queste interrogano attivamente il `CombatManager` per leggere gli HP correnti e risponde restituendo un oggetto `BossMove` al sistema.

- **`PlayerProfile`**
Incapsula lo stato macroscopico del giocatore fuori dalla battaglia. Tiene traccia dei frammenti, della progressione contro i boss e gestisce l'intera collezione di carte oltre al mazzo attualmente in uso. Viene interpellato dallo `Shop` per gestire i pagamenti e dal `DeckBuilder`, oltre a essere persistito su disco dal Repository.

- **`Shop`**
La sua logica genera offerte pseudo-casuali pescando dal catalogo e calcola dinamicamente i prezzi di vendita. Dialoga costantemente con il `PlayerProfile` per convalidare le transazioni e trasferire le carte acquistate.

### 3.2 Layer `application` e Gestione Eventi

- **`CombatOrchestrator`**
  - **Responsabilità:** Estrapolata da `GameService` in ottica SRP. Assolve all'onere esclusivo di dirigere il ciclo di vita vitale e le interazioni del `CombatManager` (inizializzazione partita, caricamento battaglia interrotta, assegnazione ricompense e sblocco carte a fine scontro).

- **`ProfileManager`**
  - **Responsabilità:** Gemello di orchestrazione estrapolato da `GameService`. Si dedica al mantenimento e al ciclo di vita del `PlayerProfile` (salvataggi/caricamenti delegando il Repository, pulizia del salvataggio e acquisto di carte dallo Shop).

- **`GameService` (Facade)**
  - **Responsabilità:** Funge da punto di accesso unificato per i livelli superiori, incanalando le chiamate verso il `CombatOrchestrator` e il `ProfileManager`. Maschera la complessità orchestrale garantendo un'interfaccia pulita e stabile.
  - **Comunicazione:** Viene iniettato (Dependency Injection) in modo sicuro nel costruttore dei Controller JavaFX.

- **`GameFactory`**
  - **Responsabilità:** Sebbene i pattern Factory globali siano stati ridotti con l'architettura a iniezione, questa classe sopravvive con un ruolo specifico: la generazione parametrica (Factory Method) dei nemici e del giocatore all'avvio, astraendo l'istanziazione basata su `EnemyArchetype`.

- **Event Bus (`GameEventBus`, `EventPublisher`, `GameEvent`, `HpChangedEvent`, ecc...)**
  - **Responsabilità:** Trasporto messaggi asincrono. L'interfaccia `EventPublisher` è usata dal Domain (per non dipendere direttamente da classi infrastrutturali). `GameEventBus` è l'implementazione concreta (Infrastructure). Gli eventi espliciti come `CardPlayedEvent` o `CombatEndedEvent` portano payload precisi.
  - **Comunicazione:** I Listener (come i Controller grafici) si registrano (`subscribe(this)`). Quando il Bus riceve un publish, fa scattare in automatico la reaction nel Controller.

### 3.3 Layer `presentation` (UI e Controller FXML)

- **Controllers: `MenuController`, `CombatController`, `ShopController`, `DeckBuilderController`, `VictoryController`**
  - **Responsabilità:** Coordinare esclusivamente le schermate FXML. Ricevono input dai bottoni (`@FXML public void onPlayClicked()`) e chiamano i metodi del `GameService`. Se c'è da mostrare una vittoria o un HP modificato, si affidano agli eventi ricevuti dal `GameEventBus`.
  - **Comunicazione:** Parlano *solo ed esclusivamente* con `GameService` e `SceneManager`. Non comunicano mai tra di loro.

- **Classi Assistenti di UI (Animation, Rendering & UX)**
  - **`CombatAnimationManager`, `EnemyCardAnimator`, `DiceAnimator`**: De-clutterizzano i Controller gestendo logiche complesse legate a `FadeTransition` e `TranslateTransition`. Responsabili dei feedback ottici (movimenti, carte nemiche che appaiono minacciose, ritardi grafici).
  - **`UINotificationManager` e `CardZoomOverlay`**: Gestiscono la User Experience (UX) avanzata. Il primo fa apparire scritte fluttuanti a schermo (come i danni subiti in rosso o cure in verde), il secondo permette l'ingrandimento in overlay delle carte.
  - **`HandViewRenderer` e `ImageCache`**: Il `HandViewRenderer` contiene complessa logica trigonometrica per disporre le carte a forma di ventaglio. L'`ImageCache` è il servizio centralizzato in RAM che previene lag di lettura da I/O salvando globalmente le texture JavaFX.
  - **`SpaceBackgroundEngine`**: Motore custom altamente ottimizzato. Utilizza un `AnimationTimer` nativo JavaFX per ricalcolare, a 60 fps costanti, un sistema particellare di nebulose radiali e stelle in 3 dimensioni simulate. Le risorse, come sfumature e gradienti (`RadialGradient`), sono caricate in cache una volta sola e non nel loop, in modo da non saturare il Garbage Collector che causerebbe microscatti/lag evidenti.

### 3.4 Layer `persistence` e `infrastructure`

- **`SceneManager` e `ViewNavigator`**
  - **Responsabilità:** `ViewNavigator` è un'interfaccia usata dai Controller per cambiare scena senza sapere nulla di JavaFX. `SceneManager` ne è l'implementazione concreta: gestisce lo `Stage` primario, carica i file `.fxml` e implementa un `ControllerFactory` dinamico che inietta automaticamente il `GameService` e se stesso (come `ViewNavigator`) nel costruttore di ogni Controller.
  - **Comunicazione:** Richiamato dai Controller grafici.

- **`AppLauncher`**
  - **Responsabilità:** Il big bang del sistema (`public static void main`). Assegna i collegamenti "Hard-coded", avvia le impostazioni del *Primary Stage* (Fullscreen, Icone applicazione native dell'OS) e richiama il Menu.

- **`JsonGameStateRepository` / `JsonCardCatalog`**
  - **Responsabilità:** Implementano interfacce logiche. Gestiscono flussi I/O su file system. Usano la libreria *Gson* capace di sfruttare la reflection per tradurre array Java e strutture dati polimorfiche in formato testo JSON (es. `savegame.json` o lettura di `cards.json`).
  - **Comunicazione:** Leggono e scrivono. Sollevano eccezioni passandole all'Application Layer in caso di file non trovato o corrotto.

---

## 4. Organizzazione dei Dati e Persistenza

La persistenza è basata su **Gson (Google JSON)** per la sua facilità di parsing e lettura.

1. **Il Catalogo (`cards.json`):** 
   Un file master letto in modalità *read-only* all'avvio. Contiene tutte le carte teoricamente esistenti nel gioco (ID, nome, stats). Viene caricato in un `JsonCardCatalog` che funge da dizionario.
2. **Il Salvataggio (`savegame.json`):**
   Rappresenta il *Record* `GameState` serializzato in modo rigoroso.
   A differenza di un salvataggio basilare, non memorizza solo il `PlayerProfile` (collezione e frammenti), ma cattura dinamicamente l'esatto stato di un combattimento in corso (HP e Focus attuali di giocatore e nemico). In questo modo, qualora il gioco venga interrotto a metà battaglia, il `GameService` ripristinerà perfettamente lo scontro in atto dal punto esatto. Gson gestisce autonomamente liste e tipi base.
> [!WARNING]
> La manomissione manuale del file `savegame.json` da parte dell'utente finale può causare la corruzione della struttura dati in fase di deserializzazione, compromettendo irrimediabilmente i progressi di gioco.

---

## 5. Dinamiche di Gioco e Compendio delle Carte

Il ciclo di gioco (*gameplay loop*) è strutturato in una delicata danza a turni tra il Giocatore e i Boss governati dall'IA.

### 5.1 Preparazione e Dinamica di Combattimento

- **Fase di Preparazione (Deck Building):** Prima di avviare l'incontro, è richiesto al giocatore di comporre il proprio mazzo di combattimento tramite l'apposito menu. L'utente ha la facoltà di selezionare le carte da includere attingendo direttamente dalla propria collezione privata, la quale si arricchisce progressivamente tramite gli acquisti effettuati nel Negozio Cosmico. Questa fase di pre-combattimento garantisce un'elevata personalizzazione strategica dell'approccio alla battaglia.
>[!TIP]
>La composizione del mazzo è critica per il successo. Un bilanciamento ottimale tra carte a basso costo in Focus (per l'estensione delle sequenze) e attacchi ad alto costo è imperativo per sconfiggere i Boss di livello avanzato.

- **Il Turno del Giocatore ("Tessitore di Fati"):**
  - **Limiti Iniziali:** Il giocatore inizia la run con **45 HP Max** e un serbatoio massimo di **15 punti Focus**. La collezione e il mazzo iniziale sono composti da 5 carte base: *Risonanza Eterea, Fenditura Quantica, Sovraccarico Biologico, Innesto Simbiotico, Assimilazione Oscura*.
  - **Avvio:** All'inizio del suo turno, il giocatore rigenera i suoi punti *Focus* (l'energia vitale necessaria per invocare le carte) e pesca fino ad avere un set di carte a disposizione nella mano.
  - **Azione e Risoluzione:** Il giocatore può selezionare le carte che desidera, nel limite dei punti Focus disponibili. Diversamente da altri deck-builder, non c'è un buffer passivo di sospensione: quando una carta viene giocata, **il suo effetto si risolve istantaneamente**. Se la carta richiede un tiro di dadi (es. *Fenditura Quantica*), compare un dado a schermo su cui cliccare. Il risultato viene subito computato sull'AURA del bersaglio. Se i punti salute del nemico si azzerano durante questa fase, la battaglia si conclude immediatamente assegnando la vittoria e i Frammenti di Etere. Terminate le mosse e cliccato sul pulsante "Passa Turno", si entra nella End Phase.
  
- **Il Turno del Boss (L'Intelligenza Artificiale):**
  - Una volta risolto il dado, tocca al Boss. Egli non ha un vero e proprio mazzo "fisico" o una riserva di Focus di cui preoccuparsi. Ha invece un pool di mosse (*Pattern*).
  - La classe `BossAI` analizza lo stato della plancia: se gli HP del boss sono criticamente bassi, tenderà statisticamente a pescare mosse di Cura (es. *Scaglie di Polvere di Stelle*). Se il giocatore è in fin di vita, sfodererà attacchi devastanti (es. *Esecuzione Stellare*). La mossa viene eseguita in modalità "Flat" (senza dado, per non inserire troppa doppia aleatorietà ai danni subiti dal giocatore) e il turno torna al Tessitore di Fati.

### 5.2 Compendio delle Carte ed Effetti Specifici

Tutte le interazioni si basano su un set predefinito di Carte archiviate nel file `cards.json`.

**Carte del Giocatore (Starter Deck e Negozio)**
- **Risonanza Eterea (Costo 1 Focus):** `RESTORE_FOCUS`. Nessun danno applicato. Rigenera immediatamente 1 punto Focus al giocatore, permettendo di estendere le sequenze del turno.
- **Fenditura Quantica (Costo 2 Focus):** `DAMAGE`. Attacco standard. Il bersaglio subisce danni pari al risultato del lancio del D6, a cui si sommano 3 punti di danno base fisso.
- **Sovraccarico Biologico (Costo 4 Focus):** `DAMAGE_AND_SELF_DAMAGE`. Carta ad alto impatto tattico. Infligge al bersaglio l'esito del D6 incrementato di 2 danni base, ma genera un contraccolpo che infligge 2 danni all'AURA del giocatore stesso. Richiede una gestione oculata.
>[!CAUTION]
>L'utilizzo indiscriminato del Sovraccarico Biologico in condizioni di salute critica può provocare l'azzeramento volontario degli HP del giocatore, portando all'immediata sconfitta.
- **Innesto Simbiotico (Costo 3 Focus):** `HEAL`. Effetto curativo. Ripristina l'AURA del giocatore di una quantità pari all'esito del D6 incrementato di 1. Fondamentale per il sostentamento prolungato.
- **Assimilazione Oscura (Costo 0 Focus):** `SELF_DAMAGE_AND_FOCUS`. Carta di sacrificio strategico. Non consuma Focus e non prevede il lancio dei dadi. Il giocatore subisce deliberatamente 3 danni fissi all'AURA per ottenere una ricarica istantanea di 4 punti Focus. Essenziale per le strategie di chiusura rapida del turno (OTK).
- **Egida del Vuoto (Acquistabile nello Shop - 50 Etere, Costo 2 Focus):** `HEAL`. Cura l'AURA di 3 punti fissi sommati all'esito del D6.
- **Supernova Tascabile (Acquistabile nello Shop - 100 Etere, Costo 5 Focus):** `DAMAGE_MULTIPLIER`. L'attacco dal potenziale maggiore. Infligge 10 danni base sommati al D6, moltiplicando l'intero ammontare per 2. Estremamente efficace contro i nemici con statistiche inferiori.

**Le Mosse dei Boss (IA)**
Le mosse nemiche non scalano col D6, ma infliggono/curano valori "Flat" diretti, rendendo il danno nemico prevedibile e permettendo al giocatore di fare calcoli strategici.
> [!NOTE]
> **Meccanica di Apprendimento (Sblocco):** Il giocatore può fare proprie le mosse dei boss sconfiggendoli. Tutte e 15 le mosse nemiche sono state accuratamente integrate nel file `cards.json` (impostandole con un prezzo nullo per non inquinare l'algoritmo del Negozio). Questo accorgimento permette al sistema di mostrarle inizialmente come "Carte Misteriose" (oscurate) nel Deck Builder. Una volta sconfitto il Boss associato, le carte vengono sbloccate nel Profilo del giocatore e diventano equipaggiabili a tutti gli effetti nel proprio mazzo personalizzato.
*Boss Livello 0 (Avatar dell'Entropia):*
- **Lacerazione Spaziale:** Infligge 6 danni diretti all'AURA.
- **Miasma Parassitario:** Mossa Ruba-vita (Vampirismo). Sottrae 3 HP al giocatore e cura il Boss di 3 HP contemporaneamente.
- **Intrusione Psionica / Fluttuazione Quantica / Collasso Supernova:** Attacchi minori o colpi massicci (fino a 12 danni puri per la Supernova nemica).

*Boss Livello 1 (Leviatano Cosmico):*
- **Onda d'Urto Astrale (6 danni) / Nebbia della Disperazione (4 danni) / Ruggito del Creato (10 danni netti).**
- **Risucchio Abissale:** Versione potenziata del Miasma. Ruba ben 4 HP curando altrettanto.
- **Scaglie di Polvere di Stelle:** Mossa puramente difensiva. Ripristina 6 HP all'AURA del Boss in un momento di criticità.

*Boss Livello 2 (Assassino della Nebulosa / Boss Finale):*
- **Lame di Plasma / Fendente Quantico:** Attacchi diretti estremamente veloci e letali (fino a 8 danni a colpo).
- **Veleno Etereo / Passo d'Ombra:** Combinazione di micro-cure e avvelenamenti per sfiancare il giocatore.
- **Esecuzione Stellare:** Mossa finale dell'IA. Infligge 14 danni istantanei. Impone al giocatore la chiusura rapida dello scontro o il costante mantenimento di un valore di HP prossimo al massimo, per evitare il termine prematuro della partita (Game Over).
>[!WARNING]
>L'attacco Esecuzione Stellare non è soggetto a mitigazioni derivanti dai tiri del dado. Il mancato mantenimento di un'AURA superiore alla soglia di 14 punti comporterà inevitabilmente un Game Over matematico.

---

## 6. Guide Linee per l'Estensibilità

Il progetto garantisce una rapida scalabilità orizzontale:

1. **Aggiunta di Contenuti:** L'architettura *Data-Driven* centralizzata in `cards.json` permette a game designer di bilanciare le statistiche e introdurre nuove carte nello Shop o nel mazzo base semplicemente editando il file di configurazione, demandando a `JsonCardCatalog` la traduzione logica.
2. **Evoluzione del Combattimento:** L'integrazione di regole accessorie (es. una fase "Evento Casuale" o "Fase di Pre-Azione") richiede unicamente lo sviluppo di una nuova classe implementante `TurnState`, integrata poi nel normale ciclo del `CombatManager`.
3. **Sviluppo UI:** La creazione di nuove schermate e dei relativi `.fxml` richiede un Controller con un costruttore standard `(GameService, SceneManager)`. L'infrastruttura di Injection lo innesterà autonomamente senza registrazioni manuali centralizzate.

---

## 7. Sviluppi Futuri

Nonostante la robusta architettura di base, il progetto è predisposto per l'integrazione di numerose espansioni future, tra cui:

- **Ricompense Giornaliere:** Implementare un sistema di check-in (o *Daily Rewards*) che incentivi il giocatore ad aprire il gioco ogni giorno per ottenere Frammenti di Etere extra o carte promozionali.
- **Espansione della Campagna e Nuovi Boss:** Attualmente il gioco prevede un numero limitato e ridotto di incontri. Si prevede di estendere la run roguelike con nuovi Boss, pattern comportamentali dell'IA unici e linee temporali ramificate.
- **Aggiornamenti Continui del Meta-Gioco:** Sfruttando la struttura a repository JSON, sarà possibile veicolare cicli di espansioni periodiche (nuove carte ed effetti) distribuendo semplicemente file aggiornati, mantenendo il meta costantemente dinamico.
- **Negozio Cosmico Dinamico:** Al momento lo Shop è statico. Un'evoluzione naturale consisterebbe nel rendere l'offerta dinamica e temporale, con una vetrina in rotazione periodica.
- **Sistema di Reliquie/Manufatti (Passivi):** Inserire oggetti collezionabili durante le run che alterino le regole di base (es. "Inizia ogni scontro con +1 Focus", oppure "Le cure sono aumentate del 50%").
- **Mappa a Bivi (Pathing System):** Introdurre una mappa esplorabile in cui il giocatore può scegliere il prossimo nodo (es. Scontro Normale, Scontro Elite, Evento Casuale, Riposo, Negozio Cosmico), aumentando il peso delle scelte tattiche fuori dal combattimento.
- **Classi/Personaggi Giocabili Multipli:** Permettere al giocatore di scegliere diversi "Tessitori di Fati" all'inizio della run, ciascuno dotato di un mazzo base unico, un'abilità passiva speciale e un pool di AURA e Focus differenziato.
- **Raffinamento UI/UX e Tipografia:** L'iterazione attuale rappresenta a tutti gli effetti una *Beta Release*. Lo sforzo ingegneristico è stato focalizzato deliberatamente sul corretto funzionamento dell'architettura e sulla completezza delle meccaniche logiche (Game Loop). Gli sviluppi futuri prevedono una revisione del comparto grafico, con particolare attenzione all'ottimizzazione dei layout e al corretto posizionamento/allineamento degli elementi testuali.

---

## 8. Dichiarazione Utilizzo AI (Artificial Intelligence)

Si dichiara l'impiego di strumenti avanzati basati su Large Language Models (LLMs) lungo l'intero iter di sviluppo del progetto.

- **Rifattorizzazione Architetturale:** L'AI ha svolto un ruolo analitico nell'identificazione di *Code Smells* e debito tecnico, aiutando a convertire vecchie strutture statiche verso un modello solido a Iniezione di Dipendenze e basato sul Pattern Facade e Observer. Ciononostante, classi di utilità mirata come la `GameFactory` sono state preservate su consiglio dell'AI esclusivamente per il partizionamento parametrico degli archetipi.
- **Micro-Design Logic (State & Math):** L'implementazione matematica del calcolo dei danni combinato con tiri D6 e moltiplicatori cumulativi è stata elaborata sfruttando capacità analitiche di calcolo dell'AI, così come la strutturazione del Pattern State rigoroso per impedire stati illegali nel combattimento.
- **Rendering & Ottimizzazioni JavaFX:** L'AI generativa è stata cruciale nella risoluzione di colli di bottiglia critici nel framerate. In particolare, ha individuato il memory leak e i lag spike dell'engine dello spazio, spostando l'istanziazione nativa degli oggetti JavaFX fuori dal loop dell'Animation Timer. Successivamente, l'AI ha individuato e risolto un grave problema di lag e freeze della UI nel Deck Builder, causato dal ricaricamento sincrono e continuo delle immagini dal disco fisso (I/O) ad ogni aggiornamento della schermata. L'AI ha implementato una `ConcurrentHashMap` statica (Memory Cache) in `HandViewRenderer`, permettendo il salvataggio in RAM delle texture e garantendo una reattività immediata. L'AI è intervenuta inoltre nel bilanciamento *pixel-perfect* dell'interfaccia utente, aggiungendo padding e traslazioni correttive per bilanciare otticamente l'asimmetria causata dalle scrollbar invisibili native di JavaFX (come l'allineamento al centro della griglia delle carte nel Negozio). Infine, l'AI ha generato buona parte del CSS (`style.css`) basandosi sui principi formali del Neo-Morphism e layout "Cyber-SciFi".
- **Gestione Persistenza:** Fornitura e controllo delle direttive per la serializzazione/deserializzazione corretta degli oggetti con *Gson*.
- **Asset Grafici e Illustrazioni delle Carte:** Una delle sfide maggiori per un progetto indipendente è la produzione di asset visivi di alta qualità coerenti tra loro. Per ovviare all'assenza di un team di concept artist dedicato, tutte le illustrazioni grafiche presenti sulle carte giocabili sono state interamente generate attraverso intelligenze artificiali *Text-to-Image* (es. Midjourney / DALL-E). Questo approccio è stato esteso anche a molti altri oggetti e icone dell'interfaccia grafica (UI), permettendo di mantenere una forte coerenza tematica (Cyber-SciFi e Space Fantasy) e garantendo un impatto visivo professionale pur mantenendo azzerati i tempi e i costi tipici della produzione artistica tradizionale.

Tutte le implementazioni, le architetture proposte e gli script generati dall'Intelligenza Artificiale sono stati interamente letti, revisionati, corretti, modificati, convalidati e all'occorrenza scartati dallo sviluppatore. 
