import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

import java.util.List;

public class Villain {

    private int hp;
    private Pane root;
    private Path heart;
    public Villain(int hp) {
        this.hp = hp;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
   }
   public Pane getRoot() {
        return root;


   }
   public void setRoot(Pane root) {
        this.root = root;
   }
   public Path getHeart() {
        return heart;
   }
   public void setHeart(Path heart) {
        this.heart = heart;
   }

}
