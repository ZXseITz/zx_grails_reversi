package reversi.game;

import reversi.actions.*;

import java.util.*;

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
            {-1, -1}
    };

    private static final double[][] probability = new double[][]{
//            {1.00, 0.13, 0.26, 0.24, 0.24, 0.26, 0.13, 1.00},
//            {0.13, 0.00, 0.16, 0.17, 0.17, 0.16, 0.00, 0.13},
//            {0.26, 0.16, 0.25, 0.23, 0.23, 0.25, 0.16, 0.26},
//            {0.24, 0.17, 0.23, 0.20, 0.20, 0.23, 0.17, 0.24},
//            {0.24, 0.17, 0.23, 0.20, 0.20, 0.23, 0.17, 0.24},
//            {0.26, 0.16, 0.25, 0.23, 0.23, 0.25, 0.16, 0.26},
//            {0.13, 0.00, 0.16, 0.17, 0.17, 0.16, 0.00, 0.13},
//            {1.00, 0.13, 0.26, 0.24, 0.24, 0.26, 0.13, 1.00}

            {6, 2, 3, 3, 3, 3, 2, 6},
            {2, 1, 2, 2, 2, 2, 1, 2},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {2, 1, 2, 2, 2, 2, 1, 2},
            {6, 2, 3, 3, 3, 3, 2, 6}
    };

    private BoardModel model;
//    private Map<Integer, List<PlacingAction>> pActions;

    public Board(BoardModel model) {
        this.model = model;
//        this.pActions = new HashMap<>(3);
//        for (int i = 0; i < 3; i++) {
//            pActions.putIfAbsent(i, new ArrayList<>(28));
//        }
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
        get(3, 3).setColor(Token.Color.WHITE);
        get(4, 4).setColor(Token.Color.WHITE);
        get(3, 4).setColor(Token.Color.BLACK);
        get(4, 3).setColor(Token.Color.BLACK);
        model.placedTokens = 4;
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
        boolean inBoard = false;
        int n, tx, ty;

        for (int[] loopVar : loopVars) {
            tx = source.getU();
            ty = source.getV();
            n = 0;
            do {
                tx += loopVar[0];
                ty += loopVar[1];
                n++;
                inBoard = tx >= 0 && tx < 8 && ty >= 0 && ty < 8;
            } while (inBoard && get(tx, ty).getColor() == Token.getOpposite(player));
            if (inBoard && get(tx, ty).getColor() == player && n > 1) return true;
        }
        return false;
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
        Token[] tokenLine = new Token[8];
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
                    actions.add(new PlacingAction(c, x, y));
                }
            });
            if (actions.isEmpty()) actions.add(new PassAction(c));
            return actions;
        }
        return null;
    }

    public List<Action> getPossibleActions2(Token.Color c) {
        List<Action> actions = new ArrayList<>(30);
        if (!isFinished()) {
            iterateBoard((token, x, y) -> {
                if (!token.isPlaced() && validatePlacing(token, c)) {
                    for (int i = 0; i < probability[y][x]; i++) {
                        actions.add(new PlacingAction(c, x, y));
                    }
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
    public ChangedAction submit(Action a) {
        if (a instanceof PlacingAction) {
            PlacingAction p = (PlacingAction) a;
            if (!isFinished() && p.getPlayer() == getCurrentPlayer()) {
                Token source = get(p.getX(), p.getY());
                List<Token> neighbours = detectNeighbours(source, p.getPlayer());
                Token[] neighboursArray = new Token[neighbours.size()];
                neighbours.toArray(neighboursArray);
                if (neighbours.size() > 0) {
                    source.setColor(p.getPlayer());
                    for (Token t : neighbours)
                        t.setColor(p.getPlayer());
                    swapCurrentPlayer();
                    setPrevPassed(false);
                    inc();
                    return new ChangedAction(p.getPlayer(), source, neighboursArray);
                }
            }
        } else if (a instanceof PassAction) {
            PassAction p = (PassAction) a;
            if (!isFinished() && p.getPlayer() == getCurrentPlayer() && validatePassing(p.getPlayer())) {
                swapCurrentPlayer();
                setPrevPassed(true);
                return new ChangedAction(p.getPlayer(), null, null);
            }
        }
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
                Token t = get(x, y);
                sb.append(t.isPlaced() ? (t.isWhite() ? "W " : "B ") : "U ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
