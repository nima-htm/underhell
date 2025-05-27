public class Item {
    private Player player;

    public Item(Player player) {
        this.player = player;
    }

    public void hpUp() {
        if (player.gethp() < 100 && player.gethp() > 80) {
            player.setHp(320);
        } else if (player.gethp() <= 80 && player.gethp() > 0) {
            player.setHp(player.gethp() + 20);
        }

    }
}
