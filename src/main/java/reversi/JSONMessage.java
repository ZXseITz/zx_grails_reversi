package reversi;

/**
 * Created by Claudio on 16.05.2017.
 */
abstract class JSONMessage {
    public static final int CLIENT_NEW_BOT_GAME = 0;
    public static final int CLIENT_NEW_GAME = 1;
    public static final int SERVER_INIT = 2;

    public static final int CLIENT_PLACE = 10;
    public static final int SERVER_PLACE = 11;

    public static final int CLIENT_PASS = 20;
    public static final int SERVER_PASS = 21;

    public static final int SERVER_VICTORY = 50;
    public static final int SERVER_DEFEAT = 51;
    public static final int SERVER_REMIS = 52;
}
