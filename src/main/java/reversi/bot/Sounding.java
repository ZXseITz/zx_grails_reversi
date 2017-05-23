package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Claudio on 23.05.2017.
 */
public class Sounding implements Callable<ActionRating> {
    private int soundings;
    private int victories;
    private Action action;
    private ForkJoinPool fjpool;
    private Board board;

    public Sounding(Board board, Action action, int soundings) {
        fjpool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        this.board = board;
        this.action = action;
        this.soundings = soundings;
    }

    @Override
    public ActionRating call() {
        List<Action> actions = executeAction(action, board);
        if (!board.isFinished()) {
            victories = fjpool.invoke(new FJSounding(board, action, actions, soundings));
        } else {
            int win = board.winner(action.getPlayer());
            if (win < 0) victories = Integer.MAX_VALUE; //bot wins
            else if (win > 0) victories = Integer.MIN_VALUE; //bot loses
        }
        return new ActionRating(action, victories);
    }

    private List<Action> executeAction(Action action, Board board) {
        board.submit(action);
        return board.getPossibleActions(board.getCurrentPlayer());
    }
}

