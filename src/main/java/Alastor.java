import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.Random;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javafx.scene.shape.Polygon;

import java.util.Collections;

import javafx.scene.Group;
import javafx.scene.Scene;

public class Alastor extends Villain {
    private Player p;
    AudioClip dmgtaken = new AudioClip(getClass().getResource("/sounds/damage-taken.mp3").toExternalForm());

    public Alastor(int hp) {
        super(hp);
    }

    public void setPlayer(Player p) {
        this.p = p;
    }

    AudioClip dmg_taken = new AudioClip(getClass().getResource("/sounds/damage-taken.mp3").toExternalForm());
    AudioClip hitSound = new AudioClip(getClass().getResource("/sounds/laser.wav").toExternalForm());

    public void throwSpearAll() {
        Image spearImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/spear.png")));
        int numberOfSpears = 12;
        double delayBetweenSpears = 0.7;
        Timeline timeline = new Timeline();
        for (int i = 0; i < numberOfSpears; i++) {
            KeyFrame kf = new KeyFrame(Duration.seconds(i * delayBetweenSpears), event -> {
                ImageView spear = new ImageView(spearImg);
                spear.setFitWidth(90);
                spear.setFitHeight(40);
                int randomNumberX = 50 + (int) (Math.random() * 801);
                spear.setLayoutX(randomNumberX);
                spear.setLayoutY(150);
                getRoot().getChildren().add(spear);
                hitSound.play();
                javafx.geometry.Point2D tipScene = spear.localToScene(90, 0);
                Bounds heartBounds = getHeart().localToScene(getHeart().getBoundsInLocal());
                double heartCenterX = heartBounds.getMinX() + heartBounds.getWidth() / 2;
                double heartCenterY = heartBounds.getMinY() + heartBounds.getHeight() / 2;
                double dx = heartCenterX - tipScene.getX();
                double dy = heartCenterY - tipScene.getY();
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                spear.setRotate(angle);

                Line spearHitbox = new Line(0, 40, 90, 0);
                spearHitbox.setStrokeWidth(4);
                spearHitbox.setVisible(false);
                spearHitbox.setLayoutX(spear.getLayoutX());
                spearHitbox.setLayoutY(spear.getLayoutY());
                spearHitbox.setRotate(angle);
                getRoot().getChildren().add(spearHitbox);

                TranslateTransition spearMove = new TranslateTransition(Duration.seconds(1.5), spear);
                TranslateTransition hitboxMove = new TranslateTransition(Duration.seconds(1.5), spearHitbox);
                spearMove.setToX(getHeart().getLayoutX() - spear.getLayoutX());
                spearMove.setToY(getHeart().getLayoutY() - spear.getLayoutY());
                hitboxMove.setToX(getHeart().getLayoutX() - spear.getLayoutX());
                hitboxMove.setToY(getHeart().getLayoutY() - spear.getLayoutY());

                spearMove.currentTimeProperty().addListener((ignored, ignoredOld, ignoredNew) -> {
                    Bounds hitboxBounds = spearHitbox.localToScene(spearHitbox.getBoundsInLocal());
                    Bounds heartBoundsNow = getHeart().localToScene(getHeart().getBoundsInLocal());
                    Shape intersection = Shape.intersect(spearHitbox, getHeart());
                    if (hitboxBounds.intersects(heartBoundsNow) && intersection.getBoundsInLocal().getWidth() != -1) {
                        System.out.println("Spear hit the heart!");
                        dmg_taken.play();
                        p.getdmg(15);
                        dmgtaken.play();
                        spearMove.stop();
                        hitboxMove.stop();
                        getRoot().getChildren().remove(spear);
                        getRoot().getChildren().remove(spearHitbox);
                    }
                });

                spearMove.setOnFinished(ignored -> {
                    if (getRoot().getChildren().contains(spear)) {
                        System.out.println("Spear missed.");
                        getRoot().getChildren().remove(spear);
                        getRoot().getChildren().remove(spearHitbox);
                    }
                });

                spearMove.play();
                hitboxMove.play();
            });
            timeline.getKeyFrames().add(kf);
        }

        timeline.play();
    }


    public void Laser(Rectangle battleBox, Pane root, Player p) {
        Random rand = new Random();
        int numberOfLasers = 60;
        double delayBetweenLasers = 0.3;
        Timeline timeline = new Timeline();
        Bounds bounds = battleBox.localToScene(battleBox.getBoundsInLocal());
        double boxX = bounds.getMinX();
        double boxY = bounds.getMinY();
        double boxWidth = bounds.getWidth();
        double boxHeight = bounds.getHeight();

        for (int i = 0; i < numberOfLasers; i++) {
            double delay = (i == 0) ? 1 : i * delayBetweenLasers;

            KeyFrame kf = new KeyFrame(Duration.seconds(delay), event -> {
                Line laser = new Line();
                Bounds heartBounds = getHeart().localToScene(getHeart().getBoundsInLocal());
                double heartCenterX = heartBounds.getMinX() + heartBounds.getWidth() / 2;
                double heartCenterY = heartBounds.getMinY() + heartBounds.getHeight() / 2;

                boolean horizontal = rand.nextBoolean();

                if (horizontal) {
                    double y = heartCenterY;
                    laser.setStartX(boxX);
                    laser.setStartY(y);
                    laser.setEndX(boxX + boxWidth);
                    laser.setEndY(y);
                } else {
                    double x = heartCenterX;
                    laser.setStartX(x);
                    laser.setStartY(boxY);
                    laser.setEndX(x);
                    laser.setEndY(boxY + boxHeight);
                }


                laser.setStroke(Color.BLUE);
                laser.setStrokeWidth(8);
                DropShadow glow = new DropShadow();
                glow.setColor(Color.BLUE);
                glow.setRadius(20);
                laser.setEffect(glow);
                root.getChildren().add(laser);


                PauseTransition collisionDelay = new PauseTransition(Duration.seconds(1));
                collisionDelay.setOnFinished(e -> {
                    laser.setStroke(Color.RED);
                    hitSound.play();
                    DropShadow glow0 = new DropShadow();
                    glow.setColor(Color.DARKRED);
                    glow.setRadius(20);
                    laser.setEffect(glow0);
                    Bounds laserBounds = laser.localToScene(laser.getBoundsInLocal());

                    if (laserBounds.intersects(heartBounds)) {
                        Shape intersection = Shape.intersect(laser, getHeart());
                        if (intersection.getBoundsInLocal().getWidth() != -1) {
                            System.out.println("Laser hit the heart!");
                            p.getdmg(5);
                            dmgtaken.play();
                            dmg_taken.play();
                        }
                    }
                });
                collisionDelay.play();


                PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
                pt.setOnFinished(e -> root.getChildren().remove(laser));
                pt.play();
            });
            timeline.getKeyFrames().add(kf);
        }
        timeline.play();
    }


    public void JumpyHeart(Rectangle battleBox, Path heart) {
        Pane root = getRoot();
        Scene scene = root.getScene();

        List<Group> spears = new ArrayList<>();
        List<Double> baseYs = new ArrayList<>();
        List<Double> amplitudes = new ArrayList<>();
        List<Double> speeds = new ArrayList<>();
        List<Double> phases = new ArrayList<>();
        List<Double> times = new ArrayList<>();

        Random rand = new Random();
        int[] spawnTimer = {0}, patternCounter = {0}, difficultyTimer = {0};
        double[] spearSpeed = {3};
        boolean[] isInvincible = {false};

        AnimationTimer timer = new AnimationTimer() {
            long last = 0;
            double totalTime = 0;

            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                double dt = (now - last) / 1e9;
                last = now;
                totalTime += dt;

                Bounds boxInScene = battleBox.localToScene(battleBox.getBoundsInLocal());
                double minX = boxInScene.getMinX();
                double maxX = boxInScene.getMaxX();
                double minY = boxInScene.getMinY();
                double maxY = boxInScene.getMaxY();

                if (totalTime >= 15) {
                    System.out.println("Game over: Time's up!");
                    for (Group spear : spears) root.getChildren().remove(spear);
                    spears.clear();
                    baseYs.clear();
                    amplitudes.clear();
                    speeds.clear();
                    phases.clear();
                    times.clear();
                    this.stop();
                    return;
                }

                spawnTimer[0]++;
                difficultyTimer[0]++;
                if (spawnTimer[0] % 90 == 0) {
                    double startX = minX - 40;
                    patternCounter[0]++;
                    if (patternCounter[0] % 5 == 0) {
                        for (int i = 0; i < 5; i++) {
                            double offset = i * 60;
                            for (boolean top : new boolean[]{true, false}) {
                                Polygon tip = new Polygon();
                                Rectangle shaft = new Rectangle(8, 60);
                                shaft.setFill(Color.WHITE);
                                shaft.setLayoutX(4);
                                if (top) {
                                    tip.getPoints().addAll(0.0, 0.0, 16.0, 0.0, 8.0, -24.0);
                                    tip.setLayoutY(60);
                                } else {
                                    tip.getPoints().addAll(0.0, 24.0, 16.0, 24.0, 8.0, 0.0);
                                    tip.setLayoutY(-24);
                                }
                                tip.setLayoutX(0);
                                tip.setFill(Color.WHITE);
                                Group spear = new Group(shaft, tip);
                                spear.setLayoutX(startX - offset);
                                double y = top ? minY + 10 : maxY;
                                spear.setLayoutY(y);
                                root.getChildren().add(spear);
                                spears.add(spear);
                                baseYs.add(y);
                                amplitudes.add(4 + rand.nextDouble() * 4 + spearSpeed[0] * 2);
                                speeds.add(1 + rand.nextDouble() * 3 + spearSpeed[0] * 0.5);
                                phases.add(rand.nextDouble() * Math.PI * 2);
                                times.add(1.0);
                            }
                        }
                    } else {
                        boolean allowBottom = patternCounter[0] > 8;
                        boolean allowTop = true;
                        boolean top = allowTop && rand.nextBoolean();
                        boolean bottom = allowBottom && rand.nextBoolean();
                        if (!top && !bottom) {
                            top = patternCounter[0] <= 8 || rand.nextBoolean();
                            if (!top && allowBottom) bottom = true;
                        }
                        if (top) {
                            int count = 1;
                            for (int i = 0; i < count; i++) {
                                double x = startX - i * 30;
                                double y = minY + 10;
                                boolean falling = rand.nextDouble() < 0.8;
                                Rectangle shaft = new Rectangle(8, 100);
                                shaft.setFill(Color.WHITE);
                                shaft.setLayoutX(4);
                                Polygon tip = new Polygon();
                                tip.getPoints().addAll(0.0, 0.0, 16.0, 0.0, 8.0, -24.0);
                                tip.setLayoutY(60);
                                tip.setLayoutX(0);
                                tip.setFill(Color.WHITE);
                                Group spear = new Group(shaft, tip);
                                spear.setLayoutX(x);
                                spear.setLayoutY(falling ? minY - 80 : y);
                                root.getChildren().add(spear);
                                spears.add(spear);
                                baseYs.add(falling ? spear.getLayoutY() : y);
                                amplitudes.add(falling ? 0.0 : 10 + rand.nextDouble() * 20 + spearSpeed[0] * 2);
                                speeds.add(falling ? 0.0 : 1 + rand.nextDouble() * 3 + spearSpeed[0] * 0.5);
                                phases.add(0.0);
                                times.add(1.0);
                            }
                        }
                        if (bottom) {
                            int count = 1;
                            for (int i = 0; i < count; i++) {
                                double x = startX - i * 60;
                                double y = maxY;
                                Rectangle shaft = new Rectangle(8, 120);
                                shaft.setFill(Color.WHITE);
                                shaft.setLayoutX(4);
                                Polygon tip = new Polygon();
                                tip.getPoints().addAll(0.0, 24.0, 16.0, 24.0, 8.0, 0.0);
                                tip.setLayoutY(-24);
                                tip.setLayoutX(0);
                                tip.setFill(Color.WHITE);
                                Group spear = new Group(shaft, tip);
                                spear.setLayoutX(x);
                                spear.setLayoutY(y);
                                root.getChildren().add(spear);
                                spears.add(spear);
                                baseYs.add(y);
                                amplitudes.add(4 + rand.nextDouble() * 4 + spearSpeed[0] * 2);
                                speeds.add(1 + rand.nextDouble() * 3 + spearSpeed[0] * 0.5);
                                phases.add(rand.nextDouble() * Math.PI * 2);
                                times.add(1.0);
                            }
                        }
                    }
                }

                if (difficultyTimer[0] % 600 == 0) {
                    spearSpeed[0] += 0.2;
                    System.out.println("Increased spear speed to " + spearSpeed[0]);
                }

                List<Integer> toRemove = new ArrayList<>();
                for (int i = 0; i < spears.size(); i++) {
                    Group spear = spears.get(i);
                    times.set(i, times.get(i) + dt);

                    Bounds spearBounds = spear.getBoundsInParent();
                    double proposedX = spear.getLayoutX() + spearSpeed[0];

                    if (proposedX + (spearBounds.getMinX() - spear.getLayoutX()) < minX) {
                        proposedX = minX - (spearBounds.getMinX() - spear.getLayoutX());
                    } else if (proposedX + (spearBounds.getMaxX() - spear.getLayoutX()) > maxX) {
                        proposedX = maxX - (spearBounds.getMaxX() - spear.getLayoutX());
                    }
                    spear.setLayoutX(proposedX);

                    double proposedY;
                    if (amplitudes.get(i) == 0.0 && speeds.get(i) == 0.0) {
                        proposedY = spear.getLayoutY() + spearSpeed[0] * 0.8;
                    } else {
                        proposedY = baseYs.get(i) + Math.sin(times.get(i) * speeds.get(i) + phases.get(i)) * amplitudes.get(i);
                    }

                    if (proposedY + (spearBounds.getMinY() - spear.getLayoutY()) < minY) {
                        proposedY = minY - (spearBounds.getMinY() - spear.getLayoutY());
                    } else if (proposedY + (spearBounds.getMaxY() - spear.getLayoutY()) > maxY) {
                        proposedY = maxY - (spearBounds.getMaxY() - spear.getLayoutY());
                    }
                    spear.setLayoutY(proposedY);

                    if (spear.getLayoutX() > maxX + 50) {
                        toRemove.add(i);
                    }

                    if (!isInvincible[0] && spear.getBoundsInParent().intersects(heart.getBoundsInParent())) {
                        System.out.println("Hit by spear!");
                        p.getdmg(10);
                        dmgtaken.play();
                        isInvincible[0] = true;
                        heart.setOpacity(0.5);
                        DropShadow flash = new DropShadow();
                        flash.setColor(Color.RED);
                        flash.setRadius(8);
                        heart.setEffect(flash);

                        PauseTransition removeFlash = new PauseTransition(Duration.millis(200));
                        removeFlash.setOnFinished(e -> heart.setEffect(null));
                        PauseTransition invincibilityTimer = new PauseTransition(Duration.millis(1000));
                        invincibilityTimer.setOnFinished(e -> {
                            isInvincible[0] = false;
                            heart.setOpacity(1);
                        });
                        removeFlash.play();
                        invincibilityTimer.play();
                    }
                }

                Collections.reverse(toRemove);
                for (int idx : toRemove) {
                    root.getChildren().remove(spears.get(idx));
                    spears.remove(idx);
                    baseYs.remove(idx);
                    amplitudes.remove(idx);
                    speeds.remove(idx);
                    phases.remove(idx);
                    times.remove(idx);
                }
            }
        };

        scene.getRoot().requestFocus();
        timer.start();
    }

    @Override
    public int getHp() {
        return super.getHp();
    }

    @Override
    public void setRoot(Pane root) {
        super.setRoot(root);
    }

    @Override
    public void setHeart(Path heart) {
        super.setHeart(heart);
    }

    @Override
    public void setHp(int hp) {
        super.setHp(hp);
    }
}
