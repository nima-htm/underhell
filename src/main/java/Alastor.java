import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.List;

public class Alastor extends Villain {

    public Alastor(int hp) {
        super(hp);
    }

    private Player p;

    public void setPlayer(Player p) {
        this.p = p;
    }

    public void throwSpear() {
        Polygon spear = new Polygon();
        spear.getPoints().addAll(
                0.0, 0.0,
                10.0, 5.0,
                0.0, 10.0
        );

        spear.setFill(Color.WHITE);
        int randomNumberX = 50 + (int) (Math.random() * 801);
        spear.setLayoutX(randomNumberX);
        spear.setLayoutY(150);

        getRoot().getChildren().add(spear);

        TranslateTransition spearMove = new TranslateTransition(Duration.seconds(1.5), spear);
        spearMove.setToX(getHeart().getLayoutX() - spear.getLayoutX());
        spearMove.setToY(getHeart().getLayoutY() - spear.getLayoutY());


        spearMove.setOnFinished(e -> {
            Bounds spearBounds = spear.localToScene(spear.getBoundsInLocal());
            Bounds heartBounds = getHeart().localToScene(getHeart().getBoundsInLocal());

            if (spearBounds.intersects(heartBounds)) {
                System.out.println("Spear hit the heart!");
                p.getdmg(1);
            } else {
                System.out.println("Spear missed.");

            }
            getRoot().getChildren().remove(spear);
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
