package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Claudio on 23.05.2017.
 */
public class Sounding implements Callable<ActionRating> {
    private static int nPackages = 4;

    private int soundings;
    private int victories;
    private Action action;
    private ExecutorService executor;
    private Board board;

    public Sounding(Board board, Action action, int soundings) {
        executor = Executors.newFixedThreadPool(nPackages);
        this.board = board;
        this.action = action;
        this.soundings = soundings;
    }

    @Override
    public ActionRating call() {
        try {
            board.submit(action);
            if (!board.isFinished()) {
                int s = soundings / nPackages;
                SoundingPackage[] packages = new SoundingPackage[nPackages];
                for (int i = 0; i < nPackages; i++) {
                    packages[i] = new SoundingPackage(board, action, s);
                }
                List<Future<Integer>> results = executor.invokeAll(Arrays.asList(packages));
                for (Future<Integer> result : results) {
                    victories += result.get();
                }
            } else {
                int win = board.winner(action.getPlayer());
                if (win < 0) victories = Integer.MAX_VALUE; //bot wins
                else if (win > 0) victories = Integer.MIN_VALUE; //bot loses
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ActionRating(action, victories);
    }
}

