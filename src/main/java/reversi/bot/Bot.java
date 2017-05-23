package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;
import reversi.game.Token;

import java.util.concurrent.*;

/**
 * Created by Claudio on 22.05.2017.
 */
public class Bot {
    private Token.Color color;

    public Bot(Token.Color color) {
        this.color = color;
    }

    public Future<Action> submit (Board board) {
        if (board.getCurrentPlayer() != color) throw new IllegalArgumentException("Human turn's");



        return null;
    }
}
