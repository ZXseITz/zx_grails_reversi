package reversi.json;

/**
 * Created by Claudio on 16.05.2017.
 */
public abstract class JSONMessage {
    public static final int CLIENT_NEW_GAME = 0;
    public static final int SERVER_INIT = 2;

    public static final int CLIENT_PLACE = 10;
    public static final int SERVER_PLACE_CLIENT = 11;
    public static final int SERVER_PLACE_OPPONENT = 12;

    public static final int CLIENT_PASS = 20;
    public static final int SERVER_PASS_CLIENT = 21;
    public static final int SERVER_PASS_OPPONENT = 22;

    public static final int SERVER_END = 50;

    public static class GameType {
        public static final int BOT = 0;
        public static final int PVP = 1;
    }
}
