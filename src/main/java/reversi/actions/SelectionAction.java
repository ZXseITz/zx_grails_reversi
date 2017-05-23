package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public class SelectionAction extends Action {
    private final Token[] selection;

    public SelectionAction(Token.Color player, Token[] selection) {
        super(player);
        this.selection = selection;
    }

    public Token[] getSelection() {
        return selection;
    }

    @Override
    public String toString() {
        return "Changed color: " + getPlayer() + ", selection: " + Token.arrayToString(selection);
    }
}
