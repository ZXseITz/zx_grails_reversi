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
    private ExecutorService exSubmit, exSounding;
    private ActionRating bestAction;

    public Bot(Token.Color color) {
        this.exSubmit = Executors.newSingleThreadExecutor();
        this.exSounding = Executors.newCachedThreadPool();
        this.color = color;
    }

    public Future<Action> submit (Board board) {
        if (board.getCurrentPlayer() != color) throw new IllegalArgumentException("Human turn's");
        if (board.isFinished()) throw new IllegalArgumentException("Game is already finished");
        return exSubmit.submit(() -> {
            bestAction = null;
            List<Action> pActions = board.getPossibleActions(board.getCurrentPlayer());
            List<Sounding> tasks = new ArrayList<>(pActions.size());
            for (Action action : pActions) {
                Sounding s = new Sounding(board.clone(), action, 4000);
                tasks.add(s);
            }
            List<Future<ActionRating>> ars = exSounding.invokeAll(tasks);
            for (Future<ActionRating> f : ars) {
                if (bestAction == null || bestAction.getVictories() < f.get().getVictories())
                    bestAction = f.get();
            }
            return bestAction.getAction();
        });
    }
}
