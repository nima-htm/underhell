import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Item {
    public Integer atkUp;
    private Player player;
    IntegerProperty healCount = new SimpleIntegerProperty(3);

    public Item(Player player) {
        this.player = player;
    }

    public void hpUp() {
        if (player.gethp() < 100 && player.gethp() > 80) {
            player.setHp(100);
        } else if (player.gethp() <= 80 && player.gethp() > 0) {
            player.setHp(player.gethp() + 20);
        }
    }
    public IntegerProperty getHealCount() {
        return healCount;
    }

    public int atkUp(int dmg){
        return dmg+=5;
    }
}
