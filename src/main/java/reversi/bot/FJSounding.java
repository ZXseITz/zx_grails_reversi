package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Claudio on 22.05.2017.
 */
public class FJSounding extends RecursiveTask<Integer> {
    private int soundings;
    private int victories;
    private Action action;
    private List<Action> actions, rndActions;
    private Board board;

    public FJSounding(Board board, Action action, List<Action> actions, int soundings) {
        this.rndActions = new ArrayList<>();
        this.board = board;
        this.action = action;
        this.actions = actions;
        this.soundings = soundings;
    }

    @Override
    protected Integer compute() {
        if (soundings > 1000) {
            int n = soundings / 2;
            FJSounding left = new FJSounding(board, action, actions, n);
            FJSounding right = new FJSounding(board, action, actions, n);
            left.fork();
            right.invoke();
            left.join();
            victories = left.victories + right.victories;
        } else {
            for (int i = 0; i < soundings; i++) {
                sounding(board.clone());
            }
        }
        return victories;
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
