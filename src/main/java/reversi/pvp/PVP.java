package reversi.pvp;

import reversi.game.Board;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Claudio on 25.05.2017.
 */

public class PVP {
    private final BlockingQueue<Player> pvpWhite;
    private final BlockingQueue<Player> pvpBlack;

    public PVP() {
        pvpWhite  = new ArrayBlockingQueue<>(20);
        pvpBlack  = new ArrayBlockingQueue<>(20);
        Thread matchingThread = new Thread(() -> {
            try {
                while (true) {
                    startPVP(pvpWhite.take(), pvpBlack.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        matchingThread.start();
    }

    /**
     * Adds a player to a waiting queue if he is online
     * Adds a player to a waiting queue and cancel his current game if he is ingame
     * @param player player to add
     * @param playerColor color, which the player wants to play
     * @return success
     */
    public boolean waitForMatching(Player player, Token.Color playerColor) {
        synchronized (player) {
            if (player.getState() == Player.State.INGAME) {
                if (player.getRound() instanceof RoundPVP){
                    ((RoundPVP) player.getRound()).cancel(player);
                }
                add(player, playerColor);
                return true;
            } else if (player.getState() == Player.State.ONLINE) {
                add(player, playerColor);
                return true;
            }
            return false;
        }
    }

    private void add(Player player, Token.Color playerColor) {
        if (playerColor == Token.Color.WHITE) pvpWhite.add(player);
        else pvpBlack.add(player);
        player.setState(Player.State.WAITING);
    }

    /**
     * Starts a new pvp round with 2 players
     * Adds a player again to queue, if the opponent changed to offline
     * @param white player who plays as white
     * @param black player who plays as black
     */
    private void startPVP(Player white, Player black) {
        synchronized (white) {
            synchronized (black) {
                if (white.getState() == Player.State.WAITING && black.getState() == Player.State.WAITING) {
                    Board board = new Board();
                    board.setUpBoard();
                    Round round = new RoundPVP(board, white, black);
                    white.setRound(round);
                    white.setState(Player.State.INGAME);
                    black.setRound(round);
                    black.setState(Player.State.INGAME);
                    round.start();
                } else if(white.getState() == Player.State.OFFLINE && black.getState() == Player.State.WAITING) {
                    pvpBlack.add(black);
                } else if(black.getState() == Player.State.OFFLINE && white.getState() == Player.State.WAITING) {
                    pvpWhite.add(white);
                }
            }
        }
    }
}
