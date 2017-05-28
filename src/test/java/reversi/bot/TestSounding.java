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
public class TestSounding {
    private Board board1, board2;
    private Action action;
    private Sounding sounding;

    @Before
    public void setup() {
        board1 = Mockito.mock(Board.class);
        Mockito.when(board1.submit(Mockito.any(Action.class))).thenReturn(null);

        action = new PlacingAction(Token.Color.WHITE, 4, 2);
        sounding = new Sounding(board1, action, 100);
    }

    /*
    Cannot test multi threaded, limited by mockito
    java.util.concurrent.ExecutionException: org.mockito.exceptions.misusing.WrongTypeOfReturnValue
     */

//    @Test
//    public void testCallParallel() {
//        //cannot mock sounding package
//        Mockito.when(board1.isFinished()).thenReturn(false);
//        Mockito.when(board1.clone()).then(invocation -> {
//            List<Action> list = new ArrayList<>(Arrays.asList(
//                    new PlacingAction(Token.Color.WHITE, 4, 2),
//                    new PlacingAction(Token.Color.WHITE, 2, 4)
//            ));
//            //100 / 4 => 25 variations per package
//            board2 = Mockito.mock(Board.class);
//            Mockito.when(board2.isFinished()).thenReturn(true);
//            Mockito.when(board2.submit(Mockito.any(Action.class))).thenReturn(null);
//            Mockito.when(board2.getMostLikelyActions(Mockito.any(Token.Color.class))).thenReturn(list);
//            Mockito.when(board2.winner(Mockito.any(Token.Color.class))).then(new Answer<Object>() {
//                private int count;
//                //18 victories, 3 defeats, 4 ties => 15
//                @Override
//                public Integer answer(InvocationOnMock invocation) throws Throwable {
//                    if (++count < 18) return 1;
//                    else if (++count < 21) return -1;
//                    else return 0;
//                }
//            });
//            return board2;
//        });
//
//
//        ActionRating result = sounding.call();
//        Assert.assertSame(action, result.getAction());
//        Assert.assertEquals(15 * 4, result.getVictories());
//    }

    @Test
    public void testCallFinishedVictory() {
        Mockito.when(board1.isFinished()).thenReturn(true);
        Mockito.when(board1.winner(Mockito.any(Token.Color.class))).thenReturn(1);

        ActionRating result = sounding.call();
        Assert.assertSame(action, result.getAction());
        Assert.assertEquals(Integer.MAX_VALUE, result.getVictories());
    }

    @Test
    public void testCallFinishedDefeat() {
        Mockito.when(board1.isFinished()).thenReturn(true);
        Mockito.when(board1.winner(Mockito.any(Token.Color.class))).thenReturn(-1);

        ActionRating result = sounding.call();
        Assert.assertSame(action, result.getAction());
        Assert.assertEquals(Integer.MIN_VALUE, result.getVictories());
    }

    @Test
    public void testCallFinishedTie() {
        Mockito.when(board1.isFinished()).thenReturn(true);
        Mockito.when(board1.winner(Mockito.any(Token.Color.class))).thenReturn(0);

        ActionRating result = sounding.call();
        Assert.assertSame(action, result.getAction());
        Assert.assertEquals(0, result.getVictories());
    }
}
