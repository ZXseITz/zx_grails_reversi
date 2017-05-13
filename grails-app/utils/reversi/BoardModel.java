package reversi;

/**
 * Created by Claudio on 13.05.2017.
 */
public class BoardModel {
    private final Token[][] tokens = new Token[8][8];
    public Token.Color currentPlayer;
    public int placedTokens;
    public boolean prevPassed;
    public boolean finished;

    public BoardModel() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tokens[i][j] = new Token(j, i);
            }
        }
        placedTokens = 0;
        prevPassed = false;
        finished = false;
    }

    private BoardModel(BoardModel boardModel) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.tokens[i][j] = boardModel.get(j, i).clone();
            }
        }
        placedTokens = boardModel.placedTokens;
        prevPassed = boardModel.prevPassed;
        finished = boardModel.finished;
    }

    public Token get(int x, int y) {
        return tokens[y][x];
    }

    public BoardModel clone() {
        return new BoardModel(this);
    }
}
