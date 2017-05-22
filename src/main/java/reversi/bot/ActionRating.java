package reversi.bot;

import reversi.actions.Action;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Claudio on 22.05.2017.
 */
public class ActionRating implements Comparable {
    private final AtomicInteger victories = new AtomicInteger(0);
    private Action action;

    public ActionRating(Action action) {
        this.action = action;
    }

    @Override
    public int compareTo(Object o) {
        ActionRating node = (ActionRating) o;
        return node.victories.get() - victories.get(); //reverse order
    }

    public int addValue(int n) {
        return victories.addAndGet(n);
    }

    public int getValue() {
        return victories.get();
    }

    public Action getAction() {
        return action;
    }
}
