package reversi.pvp;

import reversi.actions.ChangedAction;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;
import reversi.game.Board;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;
import reversi.json.JSONHandler;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by Claudio on 25.05.2017.
 */
public class RoundPVP extends Round {
    private Map<Player, Token.Color> players;

    public RoundPVP(Board board, Player white, Player black) {
        super(board);
        players = new HashMap<>(2);
        players.putIfAbsent(white, Token.Color.WHITE);
        players.putIfAbsent(black, Token.Color.BLACK);
    }

    public Token.Color getColor(Player player) {
        return players.getOrDefault(player, Token.Color.UNDEFINED);
    }

    public Player getPlayer(Token.Color color) {
        return players.entrySet().stream().filter(e -> e.getValue() == color).findFirst().get().getKey();
    }

    public Player getOpponent(Player player) {
        if(players.containsKey(player)) {
            return players.entrySet().stream().filter(e -> e.getKey() != player).findFirst().get().getKey();
        }
        return null;
    }

    @Override
    public void start() {
        players.keySet().forEach(player -> {
            player.setRound(this);

            Token[] selection = getSelection(getBoard(), getColor(player));
            String json = JSONHandler.buildJsonInit(getColor(player), getBoard().getPlacedTokens(), selection);
            player.send(json);
        });
    }

    @Override
    public void place(Player player, int[] xy) {
        Board board = getBoard();
        if (player.isInRound() && player.getRound() == this && getColor(player) == board.getCurrentPlayer()) {
            PlacingAction a = new PlacingAction(getColor(player), xy[0], xy[1]);
            ChangedAction changed = board.submit(a);
            if (changed != null) {
                String json = JSONHandler.buildJSONPlaceClient(changed.getPlayer(), board.getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours());
                player.send(json);

                Player opponent = getOpponent(player);
                Token[] selection = getSelection(board, getColor(opponent));
                String json2 = JSONHandler.buildJSONPlaceOpponent(changed.getPlayer(), board.getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours(), selection, !board.isFinished() ? 1 : 0);
                opponent.send(json2);

                if (board.isFinished()) {
                    sendEnding();
                }
            }
        }
    }

    @Override
    public void pass(Player player) {
        Board board = getBoard();
        if (player.isInRound() && player.getRound() == this && getColor(player) == board.getCurrentPlayer()) {
            PassAction a = new PassAction(getColor(player));
            ChangedAction changed = board.submit(a);
            if (changed != null) {
                String json = JSONHandler.buildJSONPassClient(getColor(player));
                player.send(json);

                Player opponent = getOpponent(player);
                Token[] selection = getSelection(board, getColor(opponent));
                String json2 = JSONHandler.buildJSONPassOpponent(changed.getPlayer(), selection,
                        !board.isFinished() ? 1 : 0);
                opponent.send(json2);

                if (board.isFinished()) {
                    sendEnding();
                }
            }
        }
    }

    private void sendEnding() {
        players.keySet().forEach(player -> {
            int win = getBoard().winner(getColor(player));
            String json = JSONHandler.buildJSONEnd(win);
            player.send(json);
        });
    }
}
