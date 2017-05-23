package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;
import reversi.game.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Claudio on 22.05.2017.
 */
public class Bot {
    private Token.Color color;
    private ExecutorService ex, es;
    private ActionRating bestAction;

    public Bot(Token.Color color) {
        this.ex = Executors.newSingleThreadExecutor();
        this.es = Executors.newCachedThreadPool();
        this.color = color;
    }

    public Future<Action> submit (Board board) {
        if (board.getCurrentPlayer() != color) throw new IllegalArgumentException("Human turn's");
        if (board.isFinished()) throw new IllegalArgumentException("Game is already finished");
        return ex.submit(() -> {
            bestAction = null;
            List<Action> pActions = board.getPossibleActions(board.getCurrentPlayer());
            List<Sounding> tasks = new ArrayList<>(pActions.size());
            for (Action action : pActions) {
                Sounding s = new Sounding(board.clone(), action, 2000);
                tasks.add(s);
            }
            List<Future<ActionRating>> ars = es.invokeAll(tasks);
            for (Future<ActionRating> f : ars) {
                if (bestAction == null || bestAction.getVictories() < f.get().getVictories())
                    bestAction = f.get();
            }
            return bestAction.getAction();
        });
    }
}
