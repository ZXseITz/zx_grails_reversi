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
     * Setup the board, ready for a new game
     */
    public void setUpBoard() {
        iterateBoard((token, x, y) -> {
            token.setColor(Token.Color.UNDEFINED);
            token.setHover(Token.Color.UNDEFINED);
        });
        get(3, 3).setColor(Token.Color.WHITE);
        get(4, 4).setColor(Token.Color.WHITE);
        get(3, 4).setColor(Token.Color.BLACK);
        get(4, 3).setColor(Token.Color.BLACK);

        getSelectableTokens(Token.Color.WHITE).forEach(token -> token.setHover(Token.Color.WHITE));
    }

    /**
     * Returns a list of tokens which are valid tokens to be set by the player with color c
     * @param c color of player
     * @return List of all selectable tokens
     */
    public List<Token> getSelectableTokens(Token.Color c) {
        List<Token> selectables = new ArrayList<>(20);
        if (!model.finished) {
            iterateBoard((token, x, y) -> {
                if (token.isUnplaced() && validatePlacing(token, c)) {
                    selectables.add(token);
                }
            });
        }
        return selectables;
    }

    /**
     * Validates the placing turn
     * @param source token to place
     * @param player player on turn
     * @return validation
     */
    private boolean validatePlacing(Token source, Token.Color player) {
        boolean valid = false;
        boolean validDir = false;
        boolean inBoard = false;
        int i = 0, n, tx, ty;

        do {
            tx = source.getU();
            ty = source.getV();
            n = 0;
            do {
                tx += loopVars[i][0];
                ty += loopVars[i][1];
                n++;
                inBoard = tx >= 0 && tx < 8 && ty >= 0 && ty < 8;
            } while (inBoard && get(tx, ty).getColor() == Token.getOpposite(player));
            i++;
            validDir = inBoard && get(tx, ty).getColor() == player && n > 1;
            valid = i < loopVars.length;
        } while (!validDir && valid);
        return valid;
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
