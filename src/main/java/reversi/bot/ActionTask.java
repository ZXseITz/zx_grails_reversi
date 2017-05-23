package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Claudio on 22.05.2017.
 */
public class ActionTask implements Callable<ActionRating> {
    private int soundings;
    private int victories;
    private Action action;
    private List<Action> actions, rndActions;
    private Board board;

    public ActionTask(Board board, Action action, int soundings) {
        this.rndActions = new ArrayList<>();
        this.board = board;
        this.action = action;
        this.soundings = soundings;
    }

    @Override
    public ActionRating call() throws Exception {
        actions = executeAction(action, board);
        if (!board.isFinished()) {
            for (int i = 0; i < soundings; i++) {
                sounding(board.clone());
            }
        } else {
            int win = board.winner(action.getPlayer());
            if (win < 0) victories = Integer.MAX_VALUE; //bot wins
            else if (win > 0) victories = Integer.MIN_VALUE; //bot loses
        }
        return new ActionRating(action, victories);
    }

    private void sounding(Board board) {
        rndActions.clear();
        rndActions.addAll(actions);
        while (!board.isFinished()) {
            int index = (int) (Math.random() * rndActions.size());
            Action rndAction = rndActions.get(index);
            rndActions.clear();
            rndActions.addAll(executeAction(rndAction, board));
        }
        int win = board.winner(action.getPlayer());
        if (win < 0) victories++; //bot wins
        else if (win > 0) victories--; //bot loses
    }

    private List<Action> executeAction(Action action, Board board) {
        board.submit(action);
        return board.getPossibleActions(board.getCurrentPlayer());
    }
}
