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

    public void removeFromMatching(Player player) {
        pvpWhite.remove(player);
        pvpBlack.remove(player);
    }

    public void waitForMatching(Player player, Token.Color playerColor) {
        if (playerColor == Token.Color.WHITE) pvpWhite.add(player);
        else pvpBlack.add(player);
    }

    private void startPVP(Player white, Player black) {
        Board board = new Board();
        board.setUpBoard();
        Round round = new RoundPVP(board, white, black);
        round.start();
    }
}
