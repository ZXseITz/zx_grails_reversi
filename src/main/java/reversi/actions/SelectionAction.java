package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public class SelectionAction extends Action {
    private final Token.Color player;
    private final Token[] selection;

    public SelectionAction(Token.Color player, Token[] selection) {
        this.player = player;
        this.selection = selection;
    }

    public Token.Color getPlayer() {
        return player;
    }

    public Token[] getSelection() {
        return selection;
    }
}
