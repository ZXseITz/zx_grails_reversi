package reversi.actions;

import reversi.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public class SelectionAction {
    private Token.Color player;
    private Token[] selection;

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
