package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public class PlacingAction extends Action {
    private final int x, y;

    public PlacingAction(Token.Color player, int x, int y) {
        super(player);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Placed source: " + x + ", " + y + ", " + getPlayer();
    }
}
