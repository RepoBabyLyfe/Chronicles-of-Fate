package it.unicam.cs.mpgc.rpg.matricola.presentation;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

/**
 * Utility per l'inizializzazione dello sfondo spaziale animato.
 * Elimina la duplicazione del codice di setup presente in tutti i controller.
 */
public class SpaceBackgroundInitializer {

    private SpaceBackgroundInitializer() {
        // Utility class — costruttore privato
    }

    /**
     * Configura il canvas, lo lega al ridimensionamento del pane,
     * avvia il motore di rendering e restituisce l'istanza.
     *
     * @param rootPane Il pane radice della scena.
     * @param spaceCanvas Il canvas su cui disegnare lo sfondo.
     * @return L'engine avviato, o null se i parametri sono nulli.
     */
    public static SpaceBackgroundEngine setup(StackPane rootPane, Canvas spaceCanvas) {
        if (rootPane == null || spaceCanvas == null) {
            return null;
        }

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> spaceCanvas.setWidth(newVal.doubleValue()));
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> spaceCanvas.setHeight(newVal.doubleValue()));
        spaceCanvas.setWidth(rootPane.getPrefWidth());
        spaceCanvas.setHeight(rootPane.getPrefHeight());

        SpaceBackgroundEngine engine = new SpaceBackgroundEngine(spaceCanvas);
        rootPane.setOnMouseMoved(e -> engine.updateMouseCoordinates(e.getX(), e.getY()));
        engine.start();

        return engine;
    }
}
