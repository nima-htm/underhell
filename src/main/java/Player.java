import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Player {
    private String name;
    private int level;
    private IntegerProperty hp = new SimpleIntegerProperty(100);
    private int Hp;
    Player(String name) {
        this.name = name;
    }

    Player(String name, int hp, int level) {
        this.hp.set(hp);
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setHp(int hp) {
        this.hp.set(hp);
    }

    public IntegerProperty getHp() {
        return hp;
    }


    public int gethp() {

        return getHp().get();
    }

    public void getdmg(int dmg) {
        hp.set(hp.get() - dmg);
        if (hp.get() <= 0) {
            System.out.println("You died...");
        }
    }
}
