package it.unicam.cs.mpgc.rpg123283.presentation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.BlendMode;
import java.util.ArrayList;
import java.util.List;

public class SpaceBackgroundEngine {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Star> stars = new ArrayList<>();
    private final List<NebulaCloud> nebulas = new ArrayList<>();

    private double mouseX = -1000;
    private double mouseY = -1000;

    //cache per evitare allocazione continua a 60 FPS
    private RadialGradient bgGradient;
    private double lastWidth = -1;
    private double lastHeight = -1;

    //colori delle stelle cacheadicacheati invece di ricrearli
    private static final Color STAR_COLOR_1 = Color.rgb(0, 229, 255);
    private static final Color STAR_COLOR_2 = Color.rgb(180, 50, 255);
    private static final Color STAR_COLOR_3 = Color.WHITE;

    public SpaceBackgroundEngine(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        generateUniverse();
    }

    private void generateUniverse() {
        for (int i = 0; i < 200; i++) {
            double x = Math.random() * 5000;
            double y = Math.random() * 5000;
            double radius = Math.random() * 1.5 + 0.4;
            double opacity = Math.random() * 0.8 + 0.2;
            double twinkleSpeed = 0.004 + Math.random() * 0.012;
            int layer = (int) (Math.random() * 3) + 1;
            double vx = (Math.random() - 0.5) * 0.18;
            double vy = (Math.random() - 0.4) * 0.12;

            stars.add(new Star(x, y, radius, opacity, twinkleSpeed, vx, vy, layer));
        }

        //generazione nebulose
        for (int i = 0; i < 5; i++) {
            nebulas.add(new NebulaCloud());
        }
    }

    public void start() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateAndRender();
            }
        }.start();
    }

    public void updateMouseCoordinates(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    private void updateAndRender() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        if (w <= 0 || h <= 0) return;

        //disegno dello sfondo spaziale radiale, zero-allocation nel loop
        if (bgGradient == null || w != lastWidth || h != lastHeight) {
            bgGradient = new RadialGradient(
                    0, 0, w / 2, h / 2, Math.max(w, h) * 0.75, false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#150928")),
                    new Stop(1, Color.web("#030305")));
            lastWidth = w;
            lastHeight = h;
        }

        gc.setGlobalAlpha(1.0);
        gc.setFill(bgGradient);
        gc.fillRect(0, 0, w, h);

        //nebulose
        gc.setGlobalBlendMode(BlendMode.SCREEN);
        for (NebulaCloud neb : nebulas) {
            neb.x += neb.vx;
            neb.y += neb.vy;
            neb.phase += neb.pulseSpeed;

            double currentOpacity = neb.baseOpacity + Math.sin(neb.phase) * 0.05;
            if (currentOpacity < 0) currentOpacity = 0.02;

            double viewX = ((neb.x % (w + 1200)) + (w + 1200)) % (w + 1200) - 600;
            double viewY = ((neb.y % (h + 1200)) + (h + 1200)) % (h + 1200) - 600;

            double offsetX = 0;
            double offsetY = 0;
            if (mouseX > 0 && mouseY > 0) {
                offsetX = (mouseX - w / 2) * 0.005; 
                offsetY = (mouseY - h / 2) * 0.005;
            }

            double drawX = viewX + offsetX;
            double drawY = viewY + offsetY;
            gc.setGlobalAlpha(currentOpacity);
            gc.setFill(neb.cachedGlow);
            gc.fillOval(drawX - neb.radius, drawY - neb.radius, neb.radius * 2, neb.radius * 2);
        }
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);

        //stelle
        for (Star star : stars) {
            star.x += star.vx;
            star.y += star.vy;

            star.opacity += star.twinkleSpeed * star.direction;
            if (star.opacity >= 1.0) {
                star.opacity = 1.0;
                star.direction = -1;
            }
            if (star.opacity <= 0.15) {
                star.opacity = 0.15;
                star.direction = 1;
            }

            double viewX = ((star.x % w) + w) % w;
            double viewY = ((star.y % h) + h) % h;

            double offsetX = 0;
            double offsetY = 0;

            if (mouseX > 0 && mouseY > 0) {
                offsetX = (mouseX - w / 2) * (star.layer * 0.012);
                offsetY = (mouseY - h / 2) * (star.layer * 0.012);

                double dist = Math.hypot(viewX + offsetX - mouseX, viewY + offsetY - mouseY);
                if (dist < 75 && star.layer == 3) {
                    star.opacity = Math.min(1.0, star.opacity + 0.08);
                    offsetX += ((viewX + offsetX) - mouseX) * 0.05;
                    offsetY += ((viewY + offsetY) - mouseY) * 0.05;
                }
            }

            double drawX = ((viewX + offsetX) % w + w) % w;
            double drawY = ((viewY + offsetY) % h + h) % h;
            double safeOpacity = Math.max(0.0, Math.min(1.0, star.opacity));

            Color starColor = switch (star.layer) {
                case 3 -> STAR_COLOR_1;
                case 2 -> STAR_COLOR_2;
                default -> STAR_COLOR_3;
            };

            gc.setGlobalAlpha(safeOpacity);
            gc.setFill(starColor);
            gc.fillOval(drawX, drawY, star.radius * 2, star.radius * 2);
        }
        gc.setGlobalAlpha(1.0);
    }

    private static class Star {
        double x, y, radius, opacity, twinkleSpeed, vx, vy;
        int layer;
        int direction = 1;

        Star(double x, double y, double radius, double opacity, double twinkleSpeed, double vx, double vy, int layer) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.opacity = opacity;
            this.twinkleSpeed = twinkleSpeed;
            this.vx = vx;
            this.vy = vy;
            this.layer = layer;
        }
    }

    private static class NebulaCloud {
        double x, y, radius, vx, vy, phase, pulseSpeed, baseOpacity;
        RadialGradient cachedGlow;

        NebulaCloud() {
            this.x = Math.random() * 5000;
            this.y = Math.random() * 5000;
            this.radius = 400 + Math.random() * 800; 
            this.vx = (Math.random() - 0.5) * 1.5; 
            this.vy = (Math.random() - 0.5) * 1.5;
            this.phase = Math.random() * Math.PI * 2;
            this.pulseSpeed = 0.005 + Math.random() * 0.01;
            this.baseOpacity = 0.06 + Math.random() * 0.08;

            int colorType = (int) (Math.random() * 3);
            double r, g, b;
            if (colorType == 0) { r = 0.0; g = 0.9; b = 1.0; } 
            else if (colorType == 1) { r = 0.7; g = 0.2; b = 1.0; } 
            else { r = 0.0; g = 1.0; b = 0.5; }

            this.cachedGlow = new RadialGradient(
                    0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.color(r, g, b, 1.0)),
                    new Stop(1, Color.TRANSPARENT));
        }
    }
}