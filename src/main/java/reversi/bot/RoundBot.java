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

    public Token.Color getPlayerColor() {
        return playerColor;
    }

    public Bot getBot() {
        return bot;
    }

    @Override
    public void start() {
        player.setRound(this);

        Token[] selection = getSelection(getBoard(), playerColor);
        String json = JSONHandler.buildJsonInit(playerColor, getBoard().getPlacedTokens(), selection);
        player.send(json);

        if (playerColor != Token.Color.WHITE) {
            //bot starts
            respond();
        }
    }

    @Override
    public void place(Player player, int[] xy) {
        if (player.isInRound() && player.getRound() == this && playerColor == getBoard().getCurrentPlayer()) {
            PlacingAction a = new PlacingAction(playerColor, xy[0], xy[1]);
            ChangedAction changed = getBoard().submit(a);
            if (changed != null) {
                String json = JSONHandler.buildJSONPlaceClient(changed.getPlayer(), getBoard().getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours());
                player.send(json);

                respond();
            }
        }
    }

    @Override
    public void pass(Player player) {
        if (player.isInRound() && player.getRound() == this && playerColor == getBoard().getCurrentPlayer()) {
            PassAction a = new PassAction(playerColor);
            ChangedAction changed = getBoard().submit(a);
            if (changed != null) {
                String json = JSONHandler.buildJSONPassClient(playerColor);
                player.send(json);

                respond();
            }
        }
    }

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

    private void botAction (Action action) {
        Board board = getBoard();
        if (action instanceof PlacingAction) {
            ChangedAction changed = board.submit(action);
            if (changed != null) {
                Token[] selection = getSelection(board, playerColor);
                String json = JSONHandler.buildJSONPlaceOpponent(changed.getPlayer(), board.getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours(), selection, !board.isFinished() ? 1 : 0);
                player.send(json);

                if (board.isFinished()) {
                    sendEnding();
                }
            }
        } else if (action instanceof PassAction) {
            ChangedAction changed = board.submit(action);
            if (changed != null) {
                Token[] selection = getSelection(board, playerColor);
                String json = JSONHandler.buildJSONPassOpponent(changed.getPlayer(), selection,
                        !board.isFinished() ? 1 : 0);
                player.send(json);

                if (board.isFinished()) {
                    sendEnding();
                }
            }
        }
    }

    private void sendEnding() {
        int win = getBoard().winner(playerColor);
        String json = JSONHandler.buildJSONEnd(win);
        player.send(json);
    }
}
