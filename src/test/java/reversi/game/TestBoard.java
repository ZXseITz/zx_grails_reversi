package reversi.game;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;

import java.util.List;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestBoard {
    Board board;

    @Before
    public void setup() {
        board = new Board();
    }

    @Test
    public void testSetupBoard() {
        board.setUpBoard();
        board.iterateBoard((token, x, y) -> {
            if ((x == 3 && y == 3) || (x == 4 && y == 4)) Assert.assertEquals(Token.Color.WHITE, token.getColor());
            else if ((x == 3 && y == 4) || (x == 4 && y == 3)) Assert.assertEquals(Token.Color.BLACK, token.getColor());
            else Assert.assertEquals(Token.Color.UNDEFINED, token.getColor());
            Assert.assertEquals(x.intValue(), token.getU());
            Assert.assertEquals(y.intValue(), token.getV());
        });
        Assert.assertEquals(Token.Color.WHITE, board.getCurrentPlayer());
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{2, 2}, board.getPlacedTokens());
    }

    @Test
    public void testDetectNeighbours() {
        scenario(board);
        singleDetect(board, new int[][] {{6, 1}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{4, 6}}, Token.Color.WHITE);

        singleDetect(board, new int[][] {{2, 1}, {2, 2}, {2, 3}, {2, 4}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{5, 2}, {4, 3}, {3, 4}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{6, 3}, {5, 3}, {4, 3}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{5, 4}, {4, 3}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{3, 6}, {3, 5}, {3, 4}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{1, 7}, {2, 6}, {3, 5}}, Token.Color.WHITE);
        singleDetect(board, new int[][] {{1, 2}, {2, 2}, {2, 3}, {3, 4}}, Token.Color.WHITE);

        singleDetect(board, new int[][] {{6, 1}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{3, 6}}, Token.Color.BLACK);

        singleDetect(board, new int[][] {{3, 1}, {3, 2}, {3, 3}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{4, 2}, {3, 3}, {3, 2}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{5, 6}, {4, 5}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{5, 4}, {4, 4}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{4, 6}, {4, 5}, {4, 4}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{1, 6}, {2, 5}}, Token.Color.BLACK);
        singleDetect(board, new int[][] {{1, 5}, {2, 5}}, Token.Color.BLACK);
    }

    private void singleDetect(Board board, int[][] tokens, Token.Color color) {
        List<Token> list = board.detectNeighbours(board.get(tokens[0][0], tokens[0][1]), color);
        Assert.assertEquals(list.size(), tokens.length - 1);
        for (int i = 1; i < tokens.length; i++) {
            Assert.assertSame(board.get(tokens[i][0], tokens[i][1]), list.get(i - 1));
        }
    }

    @Test
    public void testGetSelectableTokens() {
        scenario(board);
        int[][] tw = new int[][] {{1, 1}, {2, 1}, {1, 2}, {4, 2}, {5, 2}, {6, 2},
                {1, 3}, {6, 3}, {1, 4}, {5, 4}, {1, 5}, {3, 6}, {1, 7}, {2, 7}};
        List<Token> lw = board.getSelectableTokens(Token.Color.WHITE);
        Assert.assertEquals(tw.length, lw.size());
        for (int i = 0; i < tw.length; i++) {
            Assert.assertSame(board.get(tw[i][0], tw[i][1]), lw.get(i));
        }

        List<Token> lb = board.getSelectableTokens(Token.Color.BLACK);
        Assert.assertEquals(0, lb.size());
    }

    @Test
    public void testSubmit() {
        board.setUpBoard();
        Assert.assertNull(board.submit(new PassAction(Token.Color.WHITE)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.WHITE, 0, 0)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.BLACK, 3, 2)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.WHITE);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{2, 2}, board.getPlacedTokens());

        Assert.assertNotNull(board.submit(new PlacingAction(Token.Color.WHITE, 4, 2)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.BLACK);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{4, 1}, board.getPlacedTokens());

        Assert.assertNull(board.submit(new PassAction(Token.Color.BLACK)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.BLACK, 0, 0)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.WHITE, 3, 5)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.BLACK);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{4, 1}, board.getPlacedTokens());

        Assert.assertNotNull(board.submit(new PlacingAction(Token.Color.BLACK, 3, 2)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.WHITE);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{3, 3}, board.getPlacedTokens());
    }

    @Test
    public void testClone() {
        board.setUpBoard();
        Board cloned = board.clone();
        Assert.assertNotSame(board, cloned);
        Assert.assertEquals(board.getCurrentPlayer(), cloned.getCurrentPlayer());
        Assert.assertEquals(board.hasPrevPassed(), cloned.hasPrevPassed());
        Assert.assertEquals(board.isFinished(), cloned.isFinished());
        Assert.assertNotSame(board.getPlacedTokens(), cloned.getPlacedTokens());
        Assert.assertArrayEquals(board.getPlacedTokens(), cloned.getPlacedTokens());
        cloned.iterateBoard((token, x, y) -> {
            Assert.assertNotSame(board.get(x, y), token);
            Assert.assertEquals(board.get(x, y), token);
        });
    }

    private void scenario(Board board) {
        board.get(2, 2).setColor(Token.Color.BLACK);
        board.get(2, 3).setColor(Token.Color.BLACK);
        board.get(4, 3).setColor(Token.Color.BLACK);
        board.get(5, 3).setColor(Token.Color.BLACK);
        board.get(2, 4).setColor(Token.Color.BLACK);
        board.get(3, 4).setColor(Token.Color.BLACK);
        board.get(3, 5).setColor(Token.Color.BLACK);
        board.get(2, 6).setColor(Token.Color.BLACK);

        board.get(3, 2).setColor(Token.Color.WHITE);
        board.get(3, 3).setColor(Token.Color.WHITE);
        board.get(4, 4).setColor(Token.Color.WHITE);
        board.get(2, 5).setColor(Token.Color.WHITE);
        board.get(4, 5).setColor(Token.Color.WHITE);
        board.get(5, 5).setColor(Token.Color.WHITE);
    }
}
