import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.List;

public class Alastor extends Villain{


    public Alastor(int hp) {
        super(hp);
    }

    public void throwSpear() {
        Polygon spear = new Polygon();
        spear.getPoints().addAll(
                0.0, 0.0,
                10.0, 5.0,
                0.0, 10.0
        );
        spear.setFill(Color.WHITE);
        int randomNumberX = 50 + (int)(Math.random() * 801);
        spear.setLayoutX(randomNumberX);
        spear.setLayoutY(150);

        getRoot().getChildren().add(spear);

        TranslateTransition spearMove = new TranslateTransition(Duration.seconds(1.5), spear);
        spearMove.setToX(getHeart().getLayoutX() - spear.getLayoutX());
        spearMove.setToY(getHeart().getLayoutY() - spear.getLayoutY());

        spearMove.setOnFinished(e -> {
            getRoot().getChildren().remove(spear);
            System.out.println("Spear hit!");
        });

        spearMove.play();
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
