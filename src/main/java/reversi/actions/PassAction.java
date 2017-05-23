package reversi.actions;

import reversi.game.Token;

/**
 * Created by Claudio on 22.05.2017.
 */
public class PassAction extends Action {
    public PassAction(Token.Color player) {
        super(player);
    }

    @Override
    public String toString() {
        return "Passed " + getPlayer();
    }
}
