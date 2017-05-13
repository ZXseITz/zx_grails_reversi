package reversi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Claudio on 14.02.2017.
 */
public class Board {
    private static final int[][] loopVars = new int[][]{
            {0, -1},
            {1, -1},
            {1, 0},
            {1, 1},
            {0, 1},
            {-1, 1},
            {-1, 0},
            {-1, -1},
    };

    private BoardModel model;

    public Board(BoardModel model) {
        this.model = model;
    }

    @FunctionalInterface
    public interface ItBoard {
        void apply(Token token, Integer x, Integer y);
    }

    public void iterateBoard(ItBoard function) {
        for(int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                function.apply(get(x, y), x, y);
            }
        }
    }

    protected void inc() {
        model.finished = ++model.placedTokens == 64;
    }

    protected void swapCurrentPlayer() {
        if (model.currentPlayer == Token.Color.WHITE) model.currentPlayer = Token.Color.BLACK;
        else model.currentPlayer = Token.Color.WHITE;
    }

    public Token get(int x, int y) {
        return model.get(x, y);
    }

    public Token.Color getCurrentPlayer() {
        return model.currentPlayer;
    }

    public boolean hasPrevPassed() {
        return model.prevPassed;
    }

    protected void setPrevPassed(boolean prevPassed) {
        model.prevPassed = prevPassed;
    }

    public boolean isFinished() {
        return model.finished;
    }

    /**
     * Validates the incoming action
     * @return
     */
    public boolean validate() {
        //TODO
        return false;
    }

    /**
     * Checks and executes the incoming action
     */
    public void submit() {
        //TODO
    }

    /**
     * Returns all possible unplaced token, which con be placed ba player
     * @param source
     * @param player
     * @return
     */
    public List<Token> neighbours(Token source, Token.Color player) {
        //TODO
        return null;
    }

    public Board clone() {
        return new Board(model.clone());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                String c;
                Token t = get(x, y);
                if (t.isWhite()) c = "W";
                else if (t.isBlack()) c = "B";
                else c = "U";
                sb.append(c + "  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
