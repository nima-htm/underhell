import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Item {
    private int atkInUse=0;
    public Integer atkUp;
    private Player player;
    IntegerProperty healCount = new SimpleIntegerProperty(5);
    IntegerProperty atkCount = new SimpleIntegerProperty(2);


    public Item(Player player) {
        this.player = player;
    }

    public void setAtkInUse(int atkInUse) {
        this.atkInUse = atkInUse;
    }

    public int getAtkInUse() {
        return atkInUse;
    }

    public void hpUp() {
        if (player.gethp() < 100 && player.gethp() > 80) {
            player.setHp(100);
        } else if (player.gethp() <= 80 && player.gethp() > 0) {
            player.setHp(player.gethp() + 30);
        }
    }

    public IntegerProperty getHealCount() {
        return healCount;
    }

    public void setHealCount(int healCount) {
        this.healCount.set(healCount);
    }

    public void healuse() {
        this.healCount.set(healCount.get() - 1);
    }

    public void atkuse() {
        this.atkCount.set(atkCount.get() - 1);
    }

    public IntegerProperty getAtkCount() {
        return atkCount;
    }

    public void atkUp(int a, int b) {
        player.DamageUp(a, b);
    }
}
