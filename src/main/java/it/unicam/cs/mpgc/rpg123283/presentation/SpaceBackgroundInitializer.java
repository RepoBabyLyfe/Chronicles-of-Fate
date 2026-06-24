package it.unicam.cs.mpgc.rpg123283.presentation;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;

public class SpaceBackgroundInitializer {

    private SpaceBackgroundInitializer() {
    }

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
