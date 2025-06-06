import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

public class Player {
    private String name;
    private int level;
    private IntegerProperty hp = new SimpleIntegerProperty(100);
    private ArrayList<Integer> damage = new ArrayList<Integer>();

    Player(String name, int hp, int level) {
        this.hp.set(hp);
        this.name = name;
        this.level = level;
        this.damage.add(5);
        this.damage.add(10);
    }

    public ArrayList<Integer> getDamages() {
        return damage;
    }

    public void setDamage(int a , int b) {
        this.damage.set(0,a);
        this.damage.set(1,b);
    }

    public void DamageUp(int a , int b) {
        damage.set(0,damage.get(0)+a);
        damage.add(1,damage.get(1)+b);

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
    }
}
