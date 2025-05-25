import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class Alastor extends Villain {

    public Alastor(int hp) {
        super(hp);
    }

    private Player p;
    private Timeline spearTimeline;

    public void setPlayer(Player p) {
        this.p = p;
    }

    public void throwSpear() {
        Image spearImg;
        try {
            spearImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/spear.png")));
        } catch (Exception e) {
            return;
        }
        ImageView spear = new ImageView(spearImg);
        spear.setFitWidth(90);
        spear.setFitHeight(40.);
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
                p.getdmg(10);
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
