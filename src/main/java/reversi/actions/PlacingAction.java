package reversi.actions;

import reversi.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public class PlacingAction {
    private Token.Color player;
    private Token source;
    private Token[] toChange;

    public PlacingAction(Token.Color player, Token source, Token[] toChange) {
        this.player = player;
        this.source = source;
        this.toChange = toChange;
    }

    private PlacingAction(PlacingAction action) {
        this.player = action.player;
        this.source = action.source;
        this.toChange = action.toChange.clone();
    }

    public Token.Color getPlayer() {
        return player;
    }

    public Token getSource() {
        return source;
    }

    public Token[] getToChange() {
        return toChange;
    }

    public PlacingAction clone() {
        return new PlacingAction(this);
    }
}
