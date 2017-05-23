package reversi.bot;

import reversi.actions.Action;

/**
 * Created by Claudio on 22.05.2017.
 */
public class ActionRating {
    private final int victories;
    private final Action action;

    public ActionRating(Action action, int victories) {
        this.action = action;
        this.victories = victories;
    }

    public Action getAction() {
        return action;
    }

    public int getVictories() {
        return victories;
    }
}
