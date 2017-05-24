package reversi.bot;

import reversi.actions.Action;
import reversi.game.Board;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Claudio on 22.05.2017.
 */
public class SoundingPackage implements Callable<Integer> {
    private int soundings;
    private int victories;
    private Action action;
    private Board board;

    public SoundingPackage(Board board, Action action, int soundings) {
        this.board = board;
        this.action = action;
        this.soundings = soundings;
    }

    @Override
    public Integer call() {
        for (int i = 0; i < soundings; i++) {
            sounding(board.clone());
        }
//        System.out.println(this);
        return victories;
    }

    private void sounding(Board board) {
        while (!board.isFinished()) {
            List<Action> list = board.getPossibleActions(board.getCurrentPlayer());
            int index = (int) (Math.random() * list.size());
            Action rndAction = list.get(index);
            board.submit(rndAction);
        }
        int win = board.winner(action.getPlayer());
        if (win > 0) victories++; //bot wins
        else if (win < 0) victories--; //bot loses
    }

    @Override
    public String toString() {
        return "Sounding package action: " + action + ", victories: " + victories;
    }
}
