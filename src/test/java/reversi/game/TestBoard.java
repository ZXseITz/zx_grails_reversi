package reversi.game;

import org.junit.Test;
import org.junit.Assert;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;

import java.util.List;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestBoard {
    private Board board;

    @Test
    public void testSetupBoard() {
        board = new Board();
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
        int[][] scenario = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 0, 0, 0, 0},
                {0, 0, 2, 1, 2, 2, 0, 0},
                {0, 0, 2, 2, 1, 0, 0, 0},
                {0, 0, 1, 2, 1, 1, 0, 0},
                {0, 0, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {6, 8}, false, false);
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
        Assert.assertEquals(tokens.length - 1, list.size());
        for (int i = 1; i < tokens.length; i++) {
            Assert.assertSame(board.get(tokens[i][0], tokens[i][1]), list.get(i - 1));
        }
    }

    @Test
    public void testGetSelectableTokens() {
        int[][] scenario = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 0, 0, 0, 0},
                {0, 0, 2, 1, 2, 2, 0, 0},
                {0, 0, 2, 2, 1, 0, 0, 0},
                {0, 0, 1, 2, 1, 1, 0, 0},
                {0, 0, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {6, 8}, false, false);
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
    public void testSubmitPlace() {
        int[][] scenario = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 0, 0, 0, 0},
                {0, 0, 2, 1, 2, 2, 0, 0},
                {0, 0, 2, 2, 1, 0, 0, 0},
                {0, 0, 1, 2, 1, 1, 0, 0},
                {0, 0, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {6, 8}, false, false);
        Assert.assertNull(board.submit(new PassAction(Token.Color.WHITE)));
        Assert.assertNull(board.submit(new PassAction(Token.Color.BLACK)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.WHITE, 0, 0)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.WHITE, 3, 2)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.BLACK, 3, 1)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.WHITE);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{6, 8}, board.getPlacedTokens());

        Assert.assertNotNull(board.submit(new PlacingAction(Token.Color.WHITE, 6, 3)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.BLACK);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[]{9, 6}, board.getPlacedTokens());
    }

    @Test
    public void testSubmitPass() {
        int[][] scenario = new int[][]{
                {0, 1, 2, 2, 1, 2, 2, 2},
                {0, 0, 1, 2, 2, 2, 1, 0},
                {0, 0, 1, 2, 2, 2, 1, 0},
                {0, 0, 1, 2, 2, 1, 0, 0},
                {0, 0, 1, 2, 1, 2, 1, 0},
                {0, 0, 1, 1, 2, 2, 1, 0},
                {0, 0, 0, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {17, 17}, false, false);
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.WHITE, 7, 1)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.WHITE, 7, 0)));
        Assert.assertNull(board.submit(new PlacingAction(Token.Color.BLACK, 1, 3)));
        Assert.assertNull(board.submit(new PassAction(Token.Color.BLACK)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.WHITE);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[] {17, 17}, board.getPlacedTokens());

        Assert.assertNotNull(board.submit(new PassAction(Token.Color.WHITE)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.BLACK);
        Assert.assertTrue(board.hasPrevPassed());
        Assert.assertFalse(board.isFinished());
        Assert.assertArrayEquals(new int[] {17, 17}, board.getPlacedTokens());
    }

    @Test
    public void testPlaceFinish() {
        int[][] scenario = new int[][]{
                {1, 1, 1, 1, 2, 2, 2, 2},
                {1, 1, 1, 1, 1, 1, 2, 2},
                {1, 1, 2, 1, 2, 2, 2, 1},
                {1, 1, 2, 1, 2, 2, 1, 1},
                {2, 2, 2, 2, 2, 2, 1, 1},
                {2, 2, 2, 1, 2, 1, 1, 1},
                {1, 2, 2, 2, 2, 1, 1, 1},
                {0, 2, 2, 2, 2, 1, 1, 1},
        };
        board = new Board(scenario, Token.Color.BLACK, new int[] {32, 31}, false, false);
        Assert.assertNotNull(board.submit(new PlacingAction(Token.Color.BLACK, 0, 7)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.WHITE);
        Assert.assertFalse(board.hasPrevPassed());
        Assert.assertTrue(board.isFinished());
        Assert.assertArrayEquals(new int[]{31, 33}, board.getPlacedTokens());
    }

    @Test
    public void testPassFinished() {
        int[][] scenario = new int[][]{
                {0, 1, 2, 2, 1, 2, 2, 2},
                {0, 0, 1, 2, 2, 2, 1, 0},
                {0, 0, 1, 2, 2, 2, 1, 0},
                {0, 0, 1, 2, 2, 1, 0, 0},
                {0, 0, 1, 2, 1, 2, 1, 0},
                {0, 0, 1, 1, 2, 2, 1, 0},
                {0, 0, 0, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {17, 17}, true, false);
        Assert.assertNotNull(board.submit(new PassAction(Token.Color.WHITE)));
        Assert.assertEquals(board.getCurrentPlayer(), Token.Color.BLACK);
        Assert.assertTrue(board.hasPrevPassed());
        Assert.assertTrue(board.isFinished());
        Assert.assertArrayEquals(new int[]{17, 17}, board.getPlacedTokens());
    }

    @Test
    public void testWinVictoryWhite() {
        int[][] scenario = new int[][]{
                {1, 1, 1, 1, 2, 2, 2, 2},
                {1, 1, 1, 1, 1, 1, 2, 2},
                {1, 1, 2, 1, 2, 2, 2, 1},
                {1, 1, 2, 1, 2, 2, 1, 1},
                {1, 2, 2, 2, 2, 2, 1, 1},
                {1, 2, 2, 1, 2, 1, 1, 1},
                {1, 2, 2, 2, 2, 1, 1, 1},
                {1, 2, 2, 2, 2, 1, 1, 1},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {35, 29}, false, true);
        Assert.assertEquals(1, board.winner(Token.Color.WHITE));
        Assert.assertEquals(-1, board.winner(Token.Color.BLACK));
    }

    @Test
    public void testWinDefeatWhite() {
        int[][] scenario = new int[][]{
                {1, 1, 1, 1, 2, 2, 2, 2},
                {1, 1, 1, 1, 1, 1, 2, 2},
                {1, 1, 2, 1, 2, 2, 2, 1},
                {1, 1, 2, 1, 2, 2, 1, 1},
                {2, 2, 2, 2, 2, 2, 1, 1},
                {2, 2, 2, 1, 2, 1, 1, 1},
                {2, 2, 2, 2, 2, 1, 1, 1},
                {2, 2, 2, 2, 2, 1, 1, 1},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {31, 33}, false, true);
        Assert.assertEquals(-1, board.winner(Token.Color.WHITE));
        Assert.assertEquals(1, board.winner(Token.Color.BLACK));
    }

    @Test
    public void testWinTie() {
        int[][] scenario = new int[][]{
                {1, 1, 1, 2, 2, 2, 2, 2},
                {1, 1, 1, 1, 1, 1, 2, 2},
                {1, 1, 2, 1, 2, 2, 2, 1},
                {1, 1, 2, 1, 2, 2, 1, 1},
                {2, 2, 2, 2, 2, 2, 1, 1},
                {2, 2, 2, 1, 2, 1, 1, 1},
                {2, 2, 2, 2, 2, 1, 1, 1},
                {2, 2, 2, 2, 2, 1, 1, 1},
        };
        board = new Board(scenario, Token.Color.WHITE, new int[] {32, 32}, false, true);
        Assert.assertEquals(0, board.winner(Token.Color.WHITE));
        Assert.assertEquals(0, board.winner(Token.Color.BLACK));
    }

    @Test
    public void testWinFail() {
        int[][] scenario = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 2, 1, 0, 0, 0, 0},
                {0, 0, 2, 1, 2, 2, 0, 0},
                {0, 0, 2, 2, 1, 0, 0, 0},
                {0, 0, 1, 2, 1, 1, 0, 0},
                {0, 0, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };
        try {
            board = new Board(scenario, Token.Color.WHITE, new int[] {6, 8}, false, false);
            board.winner(Token.Color.WHITE);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            Assert.assertEquals("Game is still running", e.getMessage());
        }
    }

    @Test
    public void testClone() {
        board = new Board();
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
}
