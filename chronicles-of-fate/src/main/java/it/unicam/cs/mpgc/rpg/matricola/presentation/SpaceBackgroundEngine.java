package it.unicam.cs.mpgc.rpg.matricola.presentation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import java.util.ArrayList;
import java.util.List;

public class SpaceBackgroundEngine {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Star> stars = new ArrayList<>();
    private final List<AlienShip> ships = new ArrayList<>();

    private double mouseX = -1000;
    private double mouseY = -1000;

    public SpaceBackgroundEngine(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        generateUniverse();
    }

    private void generateUniverse() {
        // 200 stelle: densità ottimale per coprire fluidamente schermi dal 720p al 4K
        for (int i = 0; i < 200; i++) {
            // Assegniamo coordinate in un universo "infinito" (non legate al canvas iniziale)
            double x = Math.random() * 5000;
            double y = Math.random() * 5000;
            double radius = Math.random() * 1.5 + 0.4;
            double opacity = Math.random() * 0.8 + 0.2;
            double twinkleSpeed = 0.004 + Math.random() * 0.012;
            int layer = (int) (Math.random() * 3) + 1; // 1 = Lontano, 3 = Vicino

            // Deriva cosmica indipendente
            double vx = (Math.random() - 0.5) * 0.18;
            double vy = (Math.random() - 0.4) * 0.12;

            stars.add(new Star(x, y, radius, opacity, twinkleSpeed, vx, vy, layer));
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

        // Disegno dello Sfondo Spaziale Radiale
        RadialGradient bgGradient = new RadialGradient(
                0, 0, w / 2, h / 2, Math.max(w, h) * 0.75, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#150928")),
                new Stop(1, Color.web("#030305"))
        );
        gc.setFill(bgGradient);
        gc.fillRect(0, 0, w, h);

        // 1. AGGIORNAMENTO E PROIEZIONE STELLE
        for (Star star : stars) {
            // Stato Dominio: La stella viaggia all'infinito (non viene MAI intrappolata)
            star.x += star.vx;
            star.y += star.vy;

            // Twinkling sicuro
            star.opacity += star.twinkleSpeed * star.direction;
            if (star.opacity >= 1.0) { star.opacity = 1.0; star.direction = -1; }
            if (star.opacity <= 0.15) { star.opacity = 0.15; star.direction = 1; }

            // Proiezione Vista: Calcoliamo la posizione a schermo dinamicamente usando il Modulo Matematico
            // Questo assicura che le stelle avvolgano lo schermo a prescindere dalle sue dimensioni attuali
            double viewX = ((star.x % w) + w) % w;
            double viewY = ((star.y % h) + h) % h;

            // Parallasse e Interazione Cinetica applicate sulle coordinate proiettate
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

            // Riapplichiamo il Modulo nel caso l'interazione le abbia spinte oltre il bordo
            double drawX = ((viewX + offsetX) % w + w) % w;
            double drawY = ((viewY + offsetY) % h + h) % h;
            double safeOpacity = Math.max(0.0, Math.min(1.0, star.opacity));

            Color starColor = switch (star.layer) {
                case 3 -> Color.rgb(0, 229, 255, safeOpacity);
                case 2 -> Color.rgb(180, 50, 255, safeOpacity);
                default -> Color.rgb(255, 255, 255, safeOpacity);
            };

            gc.setFill(starColor);
            gc.fillOval(drawX, drawY, star.radius * 2, star.radius * 2);
        }

        // 2. ASTRONAVI ALIENI BIOLUMINESCENTI (Indipendenti dalla griglia)
        if (Math.random() < 0.002 && ships.size() < 2) {
            ships.add(new AlienShip(w, h));
        }

        for (int i = ships.size() - 1; i >= 0; i--) {
            AlienShip ship = ships.get(i);
            ship.x += ship.speedX;
            ship.y += ship.speedY;

            RadialGradient glow = new RadialGradient(0, 0, ship.x, ship.y, 14, false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#ff0055", 0.45)),
                    new Stop(1, Color.TRANSPARENT));
            gc.setFill(glow);
            gc.fillOval(ship.x - 14, ship.y - 14, 28, 28);

            gc.setFill(Color.web("#ffffff", 0.85));
            gc.fillOval(ship.x - 1.5, ship.y - 1.5, 3, 3);

            if (ship.x > w + 40 || ship.x < -40 || ship.y > h + 40 || ship.y < -40) {
                ships.remove(i);
            }
        }
    }

    private static class Star {
        double x, y, radius, opacity, twinkleSpeed, vx, vy;
        int layer;
        int direction = 1;

        Star(double x, double y, double radius, double opacity, double twinkleSpeed, double vx, double vy, int layer) {
            this.x = x; this.y = y; this.radius = radius; this.opacity = opacity;
            this.twinkleSpeed = twinkleSpeed; this.vx = vx; this.vy = vy; this.layer = layer;
        }
    }

    private static class AlienShip {
        double x, y, speedX, speedY;

        AlienShip(double canvasWidth, double canvasHeight) {
            boolean fromLeft = Math.random() > 0.5;
            this.x = fromLeft ? -30 : canvasWidth + 30;
            this.y = 80 + Math.random() * (canvasHeight - 160);
            this.speedX = fromLeft ? (0.6 + Math.random() * 1.1) : -(0.6 + Math.random() * 1.1);
            this.speedY = (Math.random() - 0.5) * 0.15;
        }
    }
}