package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 22.05.2017.
 */
public abstract class Action {
    private final Token.Color player;

    public Action(Token.Color player) {
        this.player = player;
    }

    public Token.Color getPlayer() {
        return player;
    }
}
