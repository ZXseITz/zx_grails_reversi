package reversi.game;

import reversi.actions.Action;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Claudio on 14.02.2017.
 */
public class Board {
    private static final int[][] loopVars = new int[][] {
            { 0, -1 },
            { 1, -1 },
            { 1, 0 },
            { 1, 1 },
            { 0, 1 },
            { -1, 1 },
            { -1, 0 },
            { -1, -1 }
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
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                function.apply(get(x, y), x, y);
            }
        }
    }

    private void inc() {
        model.finished = ++model.placedTokens == 64;
    }

    private void swapCurrentPlayer() {
        if (model.currentPlayer == Token.Color.WHITE)
            model.currentPlayer = Token.Color.BLACK;
        else
            model.currentPlayer = Token.Color.WHITE;
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

    private void setPrevPassed(boolean prevPassed) {
        if (model.prevPassed && prevPassed) model.finished = true;
        model.prevPassed = prevPassed;
    }

    public boolean isFinished() {
        return model.finished;
    }

    /**
     * Setup the board, ready for a new Game
     */
    public void setUpBoard() {
        iterateBoard((token, x, y) -> {
            token.setColor(Token.Color.UNDEFINED);
        });
        placeToken(3, 3, Token.Color.WHITE);
        placeToken(4, 4, Token.Color.WHITE);
        placeToken(3, 4, Token.Color.BLACK);
        placeToken(4, 3, Token.Color.BLACK);
        model.placedTokens = 4;

    }

    private void placeToken(int x, int y, Token.Color c) {
        Token t = get(x, y);
        t.setColor(c);
    }

    /**
     * Returns a list of tokens which are valid tokens to be set by the player with color c
     *
     * @param c color of player
     * @return List of all selectable tokens
     */
    public List<Token> getSelectableTokens(Token.Color c) {
        List<Token> selectables = new ArrayList<>(20);
        if (!isFinished() && c == getCurrentPlayer()) {
            iterateBoard((token, x, y) -> {
                if (!token.isPlaced() && validatePlacing(token, c)) {
                    selectables.add(token);
                }
            });
        }
        return selectables;
    }

    /**
     * Validates the placing turn
     *
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

    private boolean validatePassing(Token.Color player) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Token token = get(x, y);
                if (!token.isPlaced() && validatePlacing(token, player))
                    return false;
            }
        }
        return true;
    }

    public List<Token> detectNeighbours(Token source, Token.Color player) {
        List<Token> tokenToChange = new ArrayList<>(20);
        Token[] tokenLine = new Token[6];
        for (int[] loopVar : loopVars) {
            int tx = source.getU();
            int ty = source.getV();
            int n = 0;
            boolean valid;
            do {
                tx += loopVar[0];
                ty += loopVar[1];
                valid = tx >= 0 && tx < 8 && ty >= 0 && ty < 8 && get(tx, ty).isPlaced();
            } while (valid && (tokenLine[n++] = get(tx, ty)).getColor() != player);
            if (valid && n > 1) {
                tokenToChange.addAll(Arrays.asList(tokenLine).subList(0, n - 1));
            }
        }
        return tokenToChange;
    }

    public int winner(Token.Color c) {
        int w = winner();
        if (w == 0) return 0;
        else {
            if ((c == Token.Color.WHITE && w > 0) || (c == Token.Color.BLACK && w < 0)) return 1;
            else return -1;
        }
    }

    public int winner() {
        if (!isFinished()) throw new UnsupportedOperationException("Game is still running");
        int[] array = countPlacedTokens();
        return array[0] - array[1];
    }

    public int[] countPlacedTokens() {
        int[] array = new int[2];
        array[0] = 0;
        array[1] = 0;
        iterateBoard((token, x, y) -> {
            if (token.isWhite()) array[0] += 1;
            else if (token.isBlack()) array[1] += 1;
        });
        return array;
    }

    public List<Action> getPossibleActions(Token.Color c) {
        List<Action> actions = new ArrayList<>(30);
        if (!isFinished()) {
            iterateBoard((token, x, y) -> {
                if (!token.isPlaced() && validatePlacing(token, c)) {
                    actions.add(new PlacingAction(c, token));
                }
            });
            if (actions.isEmpty()) actions.add(new PassAction(c));
            return actions;
        }
        return null;
    }

    /**
     * Checks and executes the incoming action
     */
    public boolean submit(Action a) {
        if (a instanceof PlacingAction) {
            PlacingAction p = (PlacingAction) a;
            if (!isFinished() && p.getPlayer() == getCurrentPlayer()) {
                Token source = get(p.getSource().getU(), p.getSource().getV());
                List<Token> list = detectNeighbours(p.getSource(), p.getPlayer());
                if (list.size() > 0) {
                    source.setColor(p.getPlayer());
                    for (Token t : list)
                        t.setColor(p.getPlayer());
                    swapCurrentPlayer();
                    setPrevPassed(false);
                    inc();
                    return true;
                }
            }
        } else if (a instanceof PassAction) {
            PassAction p = (PassAction) a;
            if (!isFinished() && p.getPlayer() == getCurrentPlayer() && validatePassing(p.getPlayer())) {
                swapCurrentPlayer();
                setPrevPassed(true);
                return true;
            }
        }
        return false;
    }

    public Board clone() {
        return new Board(model.clone());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Token t = get(x, y);
                sb.append(t.isPlaced() ? (t.isWhite() ? "W " : "B ") : "U ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}