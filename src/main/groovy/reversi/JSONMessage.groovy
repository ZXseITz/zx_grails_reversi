package reversi

import groovy.json.JsonBuilder

/**
 * Created by Claudio on 16.05.2017.
 */
abstract class JSONMessage {
    static int CLIENT_NEW_BOT_GAME = 0
    static int CLIENT_NEW_GAME = 1
    static int SERVER_INIT = 2

    static int CLIENT_PLACE = 10
    static int SERVER_PLACE = 11

    static int CLIENT_PASS = 20
    static int SERVER_PASS = 21

    static int SERVER_VICTORY = 50
    static int SERVER_DEFEAT = 51
    static int SERVER_REMIS = 52
}
