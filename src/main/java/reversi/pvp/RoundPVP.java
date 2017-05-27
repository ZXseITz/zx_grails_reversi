package reversi.pvp;

import reversi.actions.ChangedAction;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;
import reversi.game.Board;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by Claudio on 25.05.2017.
 */
public class RoundPVP extends Round {
    private final Map<Player, Token.Color> players;

    public RoundPVP(Board board, Player white, Player black) {
        super(board);
        players = new HashMap<>(2);
        players.putIfAbsent(white, Token.Color.WHITE);
        players.putIfAbsent(black, Token.Color.BLACK);
    }

    public Token.Color getColor(Player player) {
        return players.getOrDefault(player, Token.Color.UNDEFINED);
    }

    /**
     *
     * @param player member of this round
     * @return opponent player of this round
     */
    public Player getOpponent(Player player) {
        if (players.containsKey(player)) {
            return players.entrySet().stream().filter(e -> e.getKey() != player).findFirst().get().getKey();
        }
        return null;
    }

    /**
     * Handles the cancellation of one player
     * @param player player who left
     */
    public void cancel(Player player) {
        Player opponent = getOpponent(player);
        synchronized (opponent) {
            opponent.setRound(null);
            opponent.setState(Player.State.ONLINE);
        }
        String json = JSONHandler.buildJSONError(JSONMessage.Error.OPPONENT_DISCONNECTED);
        opponent.send(json);
    }

    /**
     * Starts a new pvp round and sends json to both players
     */
    @Override
    public void start() {
        players.keySet().forEach(player -> {
            Token[] selection = getSelection(getBoard(), getColor(player));
            String json = JSONHandler.buildJsonInit(getColor(player), getBoard().getPlacedTokens(), selection);
            player.send(json);
        });
    }

    /**
     * Handles the place action of a player
     * @param player acting player
     * @param xy {x, y} of the source token
     */
    @Override
    public void place(Player player, int[] xy) {
        Board board = getBoard();
        PlacingAction a = new PlacingAction(getColor(player), xy[0], xy[1]);
        synchronized (actionLock) {
            ChangedAction changed = board.submit(a);
            if (changed == null) {
                String json = JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION);
                player.send(json);
            } else {
                //confirm placing
                String json = JSONHandler.buildJSONPlaceClient(changed.getPlayer(), board.getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours());
                player.send(json);

                Player opponent = getOpponent(player);
                synchronized (opponent) {
                    if (opponent.getState() == Player.State.INGAME && opponent.getRound() == this) {
                        Token[] selection = getSelection(board, getColor(opponent));
                        //send placing to opponent with his selection
                        String json2 = JSONHandler.buildJSONPlaceOpponent(changed.getPlayer(), board.getPlacedTokens(),
                                changed.getSource(), changed.getNeighbours(), selection, !board.isFinished() ? 1 : 0);
                        opponent.send(json2);
                    }
                }

                if (board.isFinished()) {
                    sendEnding();
                }
            }
        }
    }

    /**
     * Handles the pass action of a player
     * @param player acting player
     */
    @Override
    public void pass(Player player) {
        Board board = getBoard();
        PassAction a = new PassAction(getColor(player));
        synchronized (actionLock) {
            ChangedAction changed = board.submit(a);
            if (changed == null) {
                String json = JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION);
                player.send(json);
            } else {
                //confirm passing
                String json = JSONHandler.buildJSONPassClient(getColor(player));
                player.send(json);

                Player opponent = getOpponent(player);
                synchronized (opponent) {
                    if (opponent.getState() == Player.State.INGAME && opponent.getRound() == this) {
                        Token[] selection = getSelection(board, getColor(opponent));
                        //send passing to opponent with his selection
                        String json2 = JSONHandler.buildJSONPassOpponent(changed.getPlayer(), selection,
                                !board.isFinished() ? 1 : 0);
                        opponent.send(json2);
                    }
                }

                if (board.isFinished()) {
                    sendEnding();
                }
            }
        }
    }

    /**
     * Handles the ending, if a round is now finished
     */
    private void sendEnding() {
        players.keySet().forEach(player -> {
            int win = getBoard().winner(getColor(player));
            String json = JSONHandler.buildJSONEnd(win);
            synchronized (player) {
                if (player.getState() == Player.State.INGAME && player.getRound() == this) {
                    player.send(json);
                    player.setRound(null);
                    player.setState(Player.State.ONLINE);
                }
            }
        });
    }
}
