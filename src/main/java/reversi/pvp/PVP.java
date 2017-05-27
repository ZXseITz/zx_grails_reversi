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
        Thread matcher = new Thread(() -> {
            try {
                while (true) {
                    startPVP(pvpWhite.take(), pvpBlack.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        matcher.start();
    }

    public boolean waitForMatching(Player player, Token.Color playerColor) {
        synchronized (player) {
            if (player.getState() == Player.State.INGAME) {
                if (player.getRound() instanceof RoundPVP){
                    ((RoundPVP) player.getRound()).disconnect(player);
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
                } else if(white.getState() == Player.State.OFFLINE) {
                    pvpBlack.add(black);
                } else if(black.getState() == Player.State.OFFLINE) {
                    pvpWhite.add(white);
                }
            }
        }
    }
}
