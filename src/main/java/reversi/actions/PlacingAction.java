package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public class PlacingAction extends Action {
    private final Token.Color player;
    private final Token source;

    public PlacingAction(Token.Color player, Token source) {
        this.player = player;
        this.source = source;
    }

    public Token.Color getPlayer() {
        return player;
    }

    public Token getSource() {
        return source;
    }
}
