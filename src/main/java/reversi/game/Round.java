package reversi.game;

import reversi.bot.Bot;
import reversi.json.JSONHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Claudio on 21.05.2017.
 */
public abstract class Round {
    private static final AtomicInteger counter = new AtomicInteger(1);

    private int id;
    private volatile Board board;

    public Round(Board board) {
        this.id = counter.getAndIncrement();
        this.board = board;
    }

    public int getId() {
        return this.id;
    }

    public Board getBoard() {
        return board;
    }

    public abstract void start();
    public abstract void place(Player player, int[] xy);
    public abstract void pass(Player player);

    protected Token[] getSelection(Board board, Token.Color color) {
        List<Token> list = board.getSelectableTokens(color);
        Token[] selection = new Token[list.size()];
        list.toArray(selection);
        return selection;
    }
}
