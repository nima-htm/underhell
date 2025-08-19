public final class GameSession {
    private static final GameSession I = new GameSession();
    public static GameSession get() { return I; }

    private Player player;

    private GameSession() {}

    public Player getPlayer() {
        if (player == null) throw new IllegalStateException("Player not set. Call GameSession.get().setPlayer(...) at game start.");
        return player;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

    // Optional: reset between runs
    public void reset() { this.player = null; }
}
