package reversi.actions;

import reversi.game.Token;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return "Changed source: " + source + ", neighbours: " + Token.arrayToString(neighbours);
    }
}
