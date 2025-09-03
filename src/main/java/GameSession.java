import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class GameSession {
    private static final GameSession I = new GameSession();
    public static GameSession get() { return I; }

    private Player player;
    Item items;

    private GameSession() {}

    public Player getPlayer() {
        if (player == null) throw new IllegalStateException("Player not set. Call GameSession.get().setPlayer(...) at game start.");
        return player;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }
    private final BooleanProperty hasKey     = new SimpleBooleanProperty(false);
    private final IntegerProperty hpPotions  = new SimpleIntegerProperty(0);
    private final IntegerProperty atkPotions = new SimpleIntegerProperty(0);

    // Optional: reset between runs
    public void reset() { this.player = null; }
    public void setItems(Item it) {
        this.items = it;
        // bind HUD counters to the Item's live properties (one-way is enough)
        hpPotions.unbind();
        atkPotions.unbind();
        if (it != null) {
            hpPotions.bind(it.getHealCount());
            atkPotions.bind(it.getAtkCount());
        }
    }
    public Item getItems() { return items; }

    // --- key / counters exposed for HUD ---
    public BooleanProperty hasKeyProperty()     { return hasKey; }
    public boolean isHasKey()                   { return hasKey.get(); }
    public void setHasKey(boolean v)            { hasKey.set(v); }

    public IntegerProperty hpPotionsProperty()  { return hpPotions; }
    public IntegerProperty atkPotionsProperty() { return atkPotions; }
}
