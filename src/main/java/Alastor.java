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

public class Alastor extends Villain {
    private Player p;

    public Alastor(int hp) {
        super(hp);
    }

    public void setPlayer(Player p) {
        this.p = p;
    }

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
                        p.getdmg(15);
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

    AudioClip hitSound = new AudioClip(getClass().getResource("/sounds/hit.wav").toExternalForm());

    public void Laser(Rectangle battleBox, Pane root, Player p) {
        Random rand = new Random();
        int numberOfLasers = 40;
        double delayBetweenLasers = 0.5;
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
//                    p.getHp().addListener((observable, oldValue, newValue) -> {
//                        if (newValue.intValue() >0) {
//                            hitSound.play();
//                        }
//                    });
                    if (laserBounds.intersects(heartBounds)) {
                        Shape intersection = Shape.intersect(laser, getHeart());
                        if (intersection.getBoundsInLocal().getWidth() != -1) {
                            System.out.println("Laser hit the heart!");
                            p.getdmg(10);
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
