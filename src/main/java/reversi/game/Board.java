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

    private static final double[][] probability = new double[][] {
            {8, 2, 3, 3, 3, 3, 2, 8},
            {2, 1, 2, 2, 2, 2, 1, 2},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {3, 2, 3, 3, 3, 3, 2, 3},
            {2, 1, 2, 2, 2, 2, 1, 2},
            {8, 2, 3, 3, 3, 3, 2, 8}
    };

    private final Token[][] tokens = new Token[8][8];
    private Token.Color currentPlayer;
    private int[] placedTokens;
    private boolean prevPassed;
    private boolean finished;

    public Board() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tokens[i][j] = new Token(j, i);
            }
        }
        currentPlayer = Token.Color.WHITE;
        placedTokens = new int[2];
        prevPassed = false;
        finished = false;
    }

    private Board(Board board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.tokens[i][j] = board.get(j, i).clone();
            }
        }
        currentPlayer = board.currentPlayer;
        placedTokens = board.placedTokens.clone();
        prevPassed = board.prevPassed;
        finished = board.finished;
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

    public Token get(int x, int y) {
        return tokens[y][x];
    }

    public Token.Color getCurrentPlayer() {
        return currentPlayer;
    }

    public int[] getPlacedTokens() {
        return placedTokens;
    }

    public boolean hasPrevPassed() {
        return prevPassed;
    }

    public boolean isFinished() {
        return finished;
    }

    private void setPrevPassed(boolean prevPassed) {
        if (this.prevPassed && prevPassed) finished = true;
        this.prevPassed = prevPassed;
    }

    private void updatePlacedTokens(int changed) {
        placedTokens[currentPlayer.getValue() - 1] += 1 + changed;
        placedTokens[Token.getOpposite(currentPlayer).getValue() - 1] -= changed;
        finished = placedTokens[0] + placedTokens[1] >= 64;
    }

    private void swapCurrentPlayer() {
        currentPlayer = Token.getOpposite(currentPlayer);
    }

    /**
     * Setup the board, ready for a new Game
     */
    public void setUpBoard() {
        iterateBoard((token, x, y) -> token.setColor(Token.Color.UNDEFINED));
        get(3, 3).setColor(Token.Color.WHITE);
        get(4, 4).setColor(Token.Color.WHITE);
        get(3, 4).setColor(Token.Color.BLACK);
        get(4, 3).setColor(Token.Color.BLACK);
        placedTokens[0] = 2;
        placedTokens[1] = 2;
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
        int[] array = getPlacedTokens();
        return array[0] - array[1];
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

    public List<Action> getMostProbablyActions(Token.Color c) {
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
                    setPrevPassed(false);
                    updatePlacedTokens(neighbours.size());
                    swapCurrentPlayer();
                    return new ChangedAction(p.getPlayer(), source, neighboursArray);
                }
            }
        } else if (a instanceof PassAction) {
            PassAction p = (PassAction) a;
            if (!isFinished() && p.getPlayer() == getCurrentPlayer() && validatePassing(p.getPlayer())) {
                setPrevPassed(true);
                swapCurrentPlayer();
                return new ChangedAction(p.getPlayer(), null, null);
            }
        }
        return null;
    }

    public Board clone() {
        return new Board(this);
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
