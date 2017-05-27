package reversi.pvp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reversi.actions.Action;
import reversi.actions.ChangedAction;
import reversi.game.Board;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestRoundPVP {
    private Player player, player2;
    private Board board;
    private RoundPVP round;

    @Before
    public void setup() {
        board = Mockito.mock(Board.class);

        player = Mockito.mock(Player.class);
        Mockito.when(player.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setState(Mockito.any(Player.State.class));
        Mockito.when(player.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setRound(Mockito.any(Round.class));
        Mockito.doNothing().when(player).send(Mockito.anyString());

        player2 = Mockito.mock(Player.class);
        Mockito.when(player2.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player2).setState(Mockito.any(Player.State.class));
        Mockito.when(player2.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player2).setRound(Mockito.any(Round.class));
        Mockito.doNothing().when(player).send(Mockito.anyString());


        round = new RoundPVP(board, player, player2);
        player.setRound(round);
        player.setState(Player.State.INGAME);
        player2.setRound(round);
        player2.setState(Player.State.INGAME);
    }

    @Test
    public void testGetOpponent() {
        Player player3 = Mockito.mock(Player.class);
        Assert.assertSame(player2, round.getOpponent(player));
        Assert.assertSame(player, round.getOpponent(player2));
        Assert.assertNull(round.getOpponent(player3));
    }

    @Test
    public void testDisconnect() {
        round.cancel(player);
        Assert.assertNull(player2.getRound());
        Assert.assertEquals(Player.State.ONLINE, player2.getState());
    }

    @Test
    public void testStart() {
        int[] placedTokens = new int[] {2, 2};
        Token[] selection = new Token[] {new Token(4, 2), new Token(5, 3), new Token(2, 4), new Token(3, 5)};
        Mockito.when(board.getPlacedTokens()).thenReturn(placedTokens);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.getSelectableTokens(Token.Color.BLACK)).thenReturn(new ArrayList<>());

        round.start();
        Mockito.verify(player).send(JSONHandler.buildJsonInit(Token.Color.WHITE, placedTokens, selection));
        Mockito.verify(player2).send(JSONHandler.buildJsonInit(Token.Color.BLACK, placedTokens, new Token[]{}));
    }

    @Test
    public void testPlaceValid() {
        int[] placedTokens = new int[] {4, 1};
        Token source = new Token(4, 2);
        Token[] neighbours = new Token[]{new Token(4, 3)};
        Token[] selection = new Token[] {new Token(3, 2), new Token(5, 2), new Token(5, 4)};
        Mockito.when(board.isFinished()).thenReturn(false);
        Mockito.when(board.getPlacedTokens()).thenReturn(placedTokens);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(new ArrayList<>());
        Mockito.when(board.getSelectableTokens(Token.Color.BLACK)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(Token.Color.WHITE, source, neighbours));

        round.place(player, new int[]{4, 2});
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceClient(Token.Color.WHITE, placedTokens, source, neighbours));
        Mockito.verify(player2).send(JSONHandler.buildJSONPlaceOpponent(Token.Color.WHITE, placedTokens, source, neighbours, selection, 1));
    }

    @Test
    public void testPlaceInvalid() {
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(null);

        round.place(player, new int[]{0, 0});
        Mockito.verify(player).send(JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION));
        Mockito.verify(player2, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    public void testPlacePlayer2offline() {
        int[] placedTokens = new int[] {4, 1};
        Token source = new Token(4, 2);
        Token[] neighbours = new Token[]{new Token(4, 3)};
        Mockito.when(board.isFinished()).thenReturn(false);
        Mockito.when(board.getPlacedTokens()).thenReturn(placedTokens);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(new ArrayList<>());
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(Token.Color.WHITE, source, neighbours));

        player2.setState(Player.State.OFFLINE);

        round.place(player, new int[]{4, 2});
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceClient(Token.Color.WHITE, placedTokens, source, neighbours));
        Mockito.verify(player2, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    public void testPlaceFinished() {
        int[] placedTokens = new int[] {24, 40};
        Token source = new Token(0, 0);
        Token[] neighbours = new Token[]{new Token(1, 1), new Token(2, 2), new Token(3, 3)};
        Mockito.when(board.isFinished()).thenReturn(true);
        Mockito.when(board.getPlacedTokens()).thenReturn(placedTokens);
        Mockito.when(board.winner(Token.Color.WHITE)).thenReturn(-1);
        Mockito.when(board.winner(Token.Color.BLACK)).thenReturn(1);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(new ArrayList<>());
        Mockito.when(board.getSelectableTokens(Token.Color.BLACK)).thenReturn(new ArrayList<>());
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(Token.Color.WHITE, source, neighbours));

        round.place(player, new int[]{0, 0});
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceClient(Token.Color.WHITE, placedTokens, source, neighbours));
        Mockito.verify(player).send(JSONHandler.buildJSONEnd(-1));
        Mockito.verify(player2).send(JSONHandler.buildJSONPlaceOpponent(Token.Color.WHITE, placedTokens, source, neighbours, new Token[]{}, 0));
        Mockito.verify(player2).send(JSONHandler.buildJSONEnd(1));
    }

    @Test
    public void testPassValid() {
        Token[] selection = new Token[] {new Token(0, 2), new Token(0, 3), new Token(0, 4)};
        Mockito.when(board.isFinished()).thenReturn(false);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(new ArrayList<>());
        Mockito.when(board.getSelectableTokens(Token.Color.BLACK)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(Token.Color.WHITE, null, null));

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONPassClient(Token.Color.WHITE));
        Mockito.verify(player2).send(JSONHandler.buildJSONPassOpponent(Token.Color.WHITE, selection, 1));
    }

    @Test
    public void testPassInvalid() {
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(null);

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION));
        Mockito.verify(player2, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    public void testPassPlayer2offline() {
        Mockito.when(board.isFinished()).thenReturn(false);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(new ArrayList<>());
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(Token.Color.WHITE, null, null));

        player2.setState(Player.State.OFFLINE);

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONPassClient(Token.Color.WHITE));
        Mockito.verify(player2, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    public void testPassFinished() {
        Token[] selection = new Token[] {new Token(0, 2), new Token(0, 3), new Token(0, 4)};
        Mockito.when(board.isFinished()).thenReturn(true);
        Mockito.when(board.winner(Token.Color.WHITE)).thenReturn(-1);
        Mockito.when(board.winner(Token.Color.BLACK)).thenReturn(1);
        Mockito.when(board.getSelectableTokens(Token.Color.WHITE)).thenReturn(new ArrayList<>());
        Mockito.when(board.getSelectableTokens(Token.Color.BLACK)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(Token.Color.WHITE, null, null));

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONPassClient(Token.Color.WHITE));
        Mockito.verify(player).send(JSONHandler.buildJSONEnd(-1));
        Mockito.verify(player2).send(JSONHandler.buildJSONPassOpponent(Token.Color.WHITE, selection, 0));
        Mockito.verify(player2).send(JSONHandler.buildJSONEnd(1));
    }
}
