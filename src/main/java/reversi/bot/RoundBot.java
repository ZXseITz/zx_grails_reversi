package reversi.bot;

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

import java.util.concurrent.Future;

/**
 * Created by Claudio on 21.05.2017.
 */
public class RoundBot extends Round {
    private Player player;
    private Token.Color playerColor;
    private Bot bot;

    public RoundBot(Board board, Player player, Token.Color color, Bot bot) {
        super(board);
        this.player = player;
        this.playerColor = color;
        this.bot = bot;
    }

    public Player getPlayer() {
        return player;
    }

    public Bot getBot() {
        return bot;
    }

    /**
     * Starts a new bot round and sends json to the players
     */
    @Override
    public void start() {
        Token[] selection = getSelection(getBoard(), playerColor);
        String json = JSONHandler.buildJsonInit(playerColor, getBoard().getPlacedTokens(), selection);
        player.send(json);

        if (playerColor != Token.Color.WHITE) {
            //bot starts
            respond();
        }
    }

    /**
     * Handles the place action of the player
     * @param player acting player
     * @param xy {x, y} of the source token
     */
    @Override
    public void place(Player player, int[] xy) {
        PlacingAction a = new PlacingAction(playerColor, xy[0], xy[1]);
        synchronized (actionLock) {
            ChangedAction changed = getBoard().submit(a);
            if (changed == null) {
                String json = JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION);
                player.send(json);
            } else {
                //confirm placing
                String json = JSONHandler.buildJSONPlaceClient(changed.getPlayer(), getBoard().getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours());
                player.send(json);

                respond();
            }
        }
    }

    /**
     * Handles the pass action of a player
     * @param player acting player
     */
    @Override
    public void pass(Player player) {
        PassAction a = new PassAction(playerColor);
        synchronized (actionLock) {
            ChangedAction changed = getBoard().submit(a);
            if (changed == null) {
                String json = JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION);
                player.send(json);
            } else {
                //confirm passing
                String json = JSONHandler.buildJSONPassClient(playerColor);
                player.send(json);

                respond();
            }
        }
    }

    /**
     * Sends ending if round is now finished
     * Request an answer from the bot
     */
    private void respond() {
        if (getBoard().isFinished()) {
            sendEnding();
        } else {
            try {
                Future<Action> reaction = getBot().submit(getBoard());
                botAction(reaction.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the bot action
     * @param action bot action
     */
    private void botAction(Action action) {
        Board board = getBoard();
        if (action instanceof PlacingAction) {
            ChangedAction changed = board.submit(action);
            Token[] selection = getSelection(board, playerColor);
            //send bot action to the client
            String json = JSONHandler.buildJSONPlaceOpponent(changed.getPlayer(), board.getPlacedTokens(),
                    changed.getSource(), changed.getNeighbours(), selection, !board.isFinished() ? 1 : 0);
            player.send(json);

            if (board.isFinished()) {
                sendEnding();
            }
        } else if (action instanceof PassAction) {
            ChangedAction changed = board.submit(action);
            Token[] selection = getSelection(board, playerColor);
            //send bot action to the client
            String json = JSONHandler.buildJSONPassOpponent(changed.getPlayer(), selection,
                    !board.isFinished() ? 1 : 0);
            player.send(json);

            if (board.isFinished()) {
                sendEnding();
            }
        }
    }

    /**
     * Handles the ending, if a round is now finished
     */
    private void sendEnding() {
        int win = getBoard().winner(playerColor);
        String json = JSONHandler.buildJSONEnd(win);
        synchronized (player) {
            player.send(json);
            player.setRound(null);
            player.setState(Player.State.ONLINE);
        }
    }
}
