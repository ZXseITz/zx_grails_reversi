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
    private List<Action> rndActions;
    private Board board;

    public FJSounding(Board board, Action action, int soundings) {
        this.rndActions = new ArrayList<>();
        this.board = board;
        this.action = action;
        this.soundings = soundings;
    }

    @Override
    protected Integer compute() {
        if (soundings > 1000) {
            int n = soundings / 2;
            FJSounding left = new FJSounding(board, action, n);
            FJSounding right = new FJSounding(board, action, n);
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
        while (!board.isFinished()) {
            rndActions.clear();
            rndActions = board.getPossibleActions(board.getCurrentPlayer());
            int index = (int) (Math.random() * rndActions.size());
            Action rndAction = rndActions.get(index);
            board.submit(rndAction);
        }
        int win = board.winner(action.getPlayer());
        if (win > 0) victories++; //bot wins
        else if (win < 0) victories--; //bot loses
    }
}
