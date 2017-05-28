package reversi.bot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import reversi.actions.Action;
import reversi.actions.ChangedAction;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;
import reversi.game.Board;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestRoundBot {
    private Player player;
    private Board board;
    private Token.Color playerColor, botColor;
    private Bot bot;
    private RoundBot round;

    @Before
    public void setup() {
        board = Mockito.mock(Board.class);

        player = Mockito.mock(Player.class);
        Mockito.when(player.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setState(Mockito.any(Player.State.class));
        Mockito.when(player.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setRound(Mockito.any(Round.class));
        Mockito.doNothing().when(player).send(Mockito.anyString());

        bot = Mockito.mock(Bot.class);
    }

    @Test
    public void testStartWhite() {
        playerColor = Token.Color.WHITE;
        int[] placedTokens = new int[] {2, 2};
        Token[] selection = new Token[] {new Token(4, 2), new Token(5, 3), new Token(2, 4), new Token(3, 5)};
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(board.getPlacedTokens()).thenReturn(placedTokens);
        Mockito.when(board.getSelectableTokens(playerColor)).thenReturn(Arrays.asList(selection));

        round.start();
        Mockito.verify(player).send(JSONHandler.buildJsonInit(playerColor, placedTokens, selection));
    }

    @Test
    public void testStartBlack() {
        playerColor = Token.Color.BLACK;
        botColor = Token.Color.WHITE;
        int[] placedTokens1 = new int[] {2, 2};
        int[] placedTokens2 = new int[] {4, 1};
        Token source = new Token(4, 2);
        Token[] neighbours = new Token[]{new Token(4, 3)};
        Token[] selection1 = new Token[] {};
        Token[] selection2 = new Token[] {new Token(3, 2), new Token(5, 2), new Token(5, 4)};
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(bot.submit(Mockito.any(Board.class))).thenReturn(createBotPlaceAction(botColor, source));
        Mockito.when(board.getPlacedTokens())
                .thenReturn(placedTokens1)
                .thenReturn(placedTokens2);
        Mockito.when(board.getSelectableTokens(playerColor))
                .thenReturn(Arrays.asList(selection1))
                .thenReturn(Arrays.asList(selection2));
        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(new ChangedAction(botColor, source, neighbours));

        round.start();
        Mockito.verify(player).send(JSONHandler.buildJsonInit(playerColor, placedTokens1, selection1));
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceOpponent(botColor, placedTokens2, source, neighbours, selection2, 1));
    }

    @Test
    public void testPlaceValid() {
        playerColor = Token.Color.WHITE;
        botColor = Token.Color.BLACK;
        int[] placedTokens1 = new int[] {4, 1};
        int[] placedTokens2 = new int[] {3, 3};
        Token source1 = new Token(4, 2);
        Token source2 = new Token(3, 2);
        Token[] neighbours1 = new Token[]{new Token(4, 3)};
        Token[] neighbours2 = new Token[]{new Token(3, 3)};
        Token[] selection = new Token[] {
                new Token(2, 1),
                new Token(2, 2),
                new Token(2, 3),
                new Token(2, 4),
                new Token(2, 5)
        };
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(bot.submit(Mockito.any(Board.class))).thenReturn(createBotPlaceAction(botColor, source2));
        Mockito.when(board.getPlacedTokens())
                .thenReturn(placedTokens1)
                .thenReturn(placedTokens2);
        Mockito.when(board.getSelectableTokens(playerColor)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.submit(Mockito.any(Action.class)))
                .thenReturn(new ChangedAction(playerColor, source1, neighbours1))
                .thenReturn(new ChangedAction(botColor, source2, neighbours2));

        round.place(player, new int[]{source1.getU(), source1.getV()});
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceClient(playerColor, placedTokens1, source1, neighbours1));
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceOpponent(botColor, placedTokens2, source2, neighbours2, selection, 1));
    }

    @Test
    public void testPlaceFinishedPlayer() {
        playerColor = Token.Color.BLACK;
        botColor = Token.Color.WHITE;
        int win = 0;
        int[] placedTokens1 = new int[] {32, 32};
        Token source1 = new Token(0, 0);
        Token[] neighbours1 = new Token[]{new Token(1, 0)};
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(board.isFinished()).thenReturn(true);
        Mockito.when(board.winner(Mockito.any(Token.Color.class))).thenReturn(win);
        Mockito.when(board.getPlacedTokens())
                .thenReturn(placedTokens1);
        Mockito.when(board.submit(Mockito.any(Action.class)))
                .thenReturn(new ChangedAction(playerColor, source1, neighbours1));

        round.place(player, new int[]{source1.getU(), source1.getV()});
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceClient(playerColor, placedTokens1, source1, neighbours1));
        Mockito.verify(player).send(JSONHandler.buildJSONEnd(win));
    }

    @Test
    public void testPlaceFinishedBot() {
        playerColor = Token.Color.WHITE;
        botColor = Token.Color.BLACK;
        int win = 1;
        int[] placedTokens1 = new int[] {40, 23};
        int[] placedTokens2 = new int[] {37, 27};
        Token source1 = new Token(1, 0);
        Token source2 = new Token(0, 0);
        Token[] neighbours1 = new Token[]{new Token(2, 0), new Token(3, 0)};
        Token[] neighbours2 = new Token[]{new Token(1, 0), new Token(2, 0), new Token(3, 0)};
        Token[] selection = new Token[] {};
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(board.isFinished())
                .thenReturn(false)
                .thenReturn(true);
        Mockito.when(board.winner(Mockito.any(Token.Color.class)))
                .thenReturn(win);
        Mockito.when(bot.submit(Mockito.any(Board.class))).thenReturn(createBotPlaceAction(botColor, source2));
        Mockito.when(board.getPlacedTokens())
                .thenReturn(placedTokens1)
                .thenReturn(placedTokens2);
        Mockito.when(board.getSelectableTokens(playerColor)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.submit(Mockito.any(Action.class)))
                .thenReturn(new ChangedAction(playerColor, source1, neighbours1))
                .thenReturn(new ChangedAction(botColor, source2, neighbours2));

        round.place(player, new int[]{source1.getU(), source1.getV()});
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceClient(playerColor, placedTokens1, source1, neighbours1));
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceOpponent(botColor, placedTokens2, source2, neighbours2, selection, 0));
        Mockito.verify(player).send(JSONHandler.buildJSONEnd(win));
    }

    @Test
    public void testPlaceInvalid() {
        playerColor = Token.Color.WHITE;
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(null);

        round.place(player, new int[]{0, 0});
        Mockito.verify(player).send(JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION));
    }

    @Test
    public void testPassValid() {
        playerColor = Token.Color.WHITE;
        botColor = Token.Color.BLACK;
        int[] placedTokens2 = new int[] {16, 38};
        Token source2 = new Token(1, 5);
        Token[] neighbours2 = new Token[] {
                new Token(2, 5),
                new Token(3, 5),
                new Token(4, 5),
                new Token(2, 4),
                new Token(3, 3),
        };
        Token[] selection = new Token[] {
                new Token(0, 5),
                new Token(0, 6)
        };
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(bot.submit(Mockito.any(Board.class))).thenReturn(createBotPlaceAction(botColor, source2));
        Mockito.when(board.getPlacedTokens())
                .thenReturn(placedTokens2);
        Mockito.when(board.getSelectableTokens(playerColor)).thenReturn(Arrays.asList(selection));
        Mockito.when(board.submit(Mockito.any(Action.class)))
                .thenReturn(new ChangedAction(playerColor, null, null))
                .thenReturn(new ChangedAction(botColor, source2, neighbours2));

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONPassClient(playerColor));
        Mockito.verify(player).send(JSONHandler.buildJSONPlaceOpponent(botColor, placedTokens2, source2, neighbours2, selection, 1));
    }


    @Test
    public void testPassFinishedPlayer() {
        playerColor = Token.Color.BLACK;
        botColor = Token.Color.WHITE;
        int win = -1;
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(board.isFinished()).thenReturn(true);
        Mockito.when(board.winner(Mockito.any(Token.Color.class))).thenReturn(win);
        Mockito.when(board.submit(Mockito.any(Action.class)))
                .thenReturn(new ChangedAction(playerColor, null, null));

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONPassClient(playerColor));
        Mockito.verify(player).send(JSONHandler.buildJSONEnd(win));
    }

    @Test
    public void testPassFinishedBot() {
        playerColor = Token.Color.WHITE;
        botColor = Token.Color.BLACK;
        int win = 1;
        Token[] selection = new Token[] {};
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(bot.submit(Mockito.any(Board.class))).thenReturn(createBotPassAction(botColor));
        Mockito.when(board.isFinished())
                .thenReturn(false)
                .thenReturn(true);
        Mockito.when(board.winner(Mockito.any(Token.Color.class))).thenReturn(win);
        Mockito.when(board.submit(Mockito.any(Action.class)))
                .thenReturn(new ChangedAction(playerColor, null, null))
                .thenReturn(new ChangedAction(botColor, null, null));

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONPassClient(playerColor));
        Mockito.verify(player).send(JSONHandler.buildJSONPassOpponent(botColor, selection, 0));
        Mockito.verify(player).send(JSONHandler.buildJSONEnd(win));
    }

    @Test
    public void testPassInvalid() {
        playerColor = Token.Color.WHITE;
        round = new RoundBot(board, player, playerColor, bot);
        player.setRound(round);
        player.setState(Player.State.INGAME);

        Mockito.when(board.submit(Mockito.any(Action.class))).thenReturn(null);

        round.pass(player);
        Mockito.verify(player).send(JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION));
    }

    private Future<Action> createBotPlaceAction(Token.Color botColor, Token source) {
        return new Future<Action>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Action get() throws InterruptedException, ExecutionException {
                return new PlacingAction(botColor, source.getU(), source.getV());
            }

            @Override
            public Action get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
    }

    private Future<Action> createBotPassAction(Token.Color botColor) {
        return new Future<Action>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Action get() throws InterruptedException, ExecutionException {
                return new PassAction(botColor);
            }

            @Override
            public Action get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
    }
}
