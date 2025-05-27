public class Item {
    private Player player;

    public Item(Player player) {
        this.player = player;
    }

    public void hpUp() {
        if (player.gethp() < 320 && player.gethp() > 270) {
            player.setHp(320);
        } else if (player.gethp() <= 270 && player.gethp() > 0) {
            player.setHp(player.gethp() + 50);
        }

    }
}
