package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 22.05.2017.
 */
public class PassAction extends Action {
    private final Token.Color player;

    public PassAction(Token.Color player) {
        this.player = player;
    }

    public Token.Color getPlayer() {
        return player;
    }
}
