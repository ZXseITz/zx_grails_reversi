package reversi.bot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import reversi.actions.Action;
import reversi.actions.PlacingAction;
import reversi.game.Board;
import reversi.game.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestSoundingPackage {
    private Board board;
    private SoundingPackage sPackage;

    @Before
    public void setup() {
        List<Action> list = new ArrayList<>(Arrays.asList(
                new PlacingAction(Token.Color.WHITE, 4, 2),
                new PlacingAction(Token.Color.WHITE, 2, 4)
        ));
        board = Mockito.mock(Board.class);
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(null);
        Mockito.when(board.getMostLikelyActions(Mockito.any(Token.Color.class))).thenReturn(list);
    }

    @Test
    public void testCall_10_60() {
        int nVariations = 10;
        int nIterations = 60;
        Mockito.when(board.clone()).then(invocation -> {
            //reset finished counter
            Mockito.when(board.isFinished()).then(new Answer<Boolean>() {
                private int count = 0;
                //nIteration iteration over board until finished
                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    return (++count == nIterations);
                }
            });
            return board;
        });
        Mockito.when(board.winner(Mockito.any(Token.Color.class))).then(new Answer<Integer>() {
            private int count = 0;
            //8 victories, 1 defeats, 1 ties => 7
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                if (++count < 8) return 1;
                else if (++count < 9) return -1;
                else return 0;
            }
        });
        sPackage = new SoundingPackage(board, new PlacingAction(Token.Color.WHITE, 4, 2), nVariations);

        Assert.assertEquals(7, sPackage.call().intValue());
        Mockito.verify(board, Mockito.times(nIterations * nVariations)).isFinished();
        Mockito.verify(board, Mockito.times(nVariations)).winner(Mockito.any(Token.Color.class));
    }

    @Test
    public void testCall_20_30() {
        int nVariations = 20;
        int nIterations = 30;
        Mockito.when(board.clone()).then(invocation -> {
            //reset finished counter
            Mockito.when(board.isFinished()).then(new Answer<Boolean>() {
                private int count = 0;
                //nIteration iteration over board until finished
                @Override
                public Boolean answer(InvocationOnMock invocation) throws Throwable {
                    return (++count == nIterations);
                }
            });
            return board;
        });
        Mockito.when(board.winner(Mockito.any(Token.Color.class))).then(new Answer<Integer>() {
            private int count = 0;
            //2 victories, 15 defeats, 3 ties => -13
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                if (++count < 15) return -1;
                else if (++count < 17) return 1;
                else return 0;
            }
        });
        sPackage = new SoundingPackage(board, new PlacingAction(Token.Color.WHITE, 4, 2), nVariations);

        Assert.assertEquals(-13, sPackage.call().intValue());
        Mockito.verify(board, Mockito.times(nIterations * nVariations)).isFinished();
        Mockito.verify(board, Mockito.times(nVariations)).winner(Mockito.any(Token.Color.class));
    }
}
