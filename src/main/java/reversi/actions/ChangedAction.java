package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 23.05.2017.
 */
public class ChangedAction extends Action {
    private final Token source;
    private final Token[] neighbours;

    public ChangedAction(Token.Color player, Token source, Token[] neighbours) {
        super(player);
        this.source = source;
        this.neighbours = neighbours;
    }

    public Token getSource() {
        return source;
    }

    public Token[] getNeighbours() {
        return neighbours;
    }
}
