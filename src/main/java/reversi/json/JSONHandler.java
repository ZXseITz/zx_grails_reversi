package reversi.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import reversi.game.Token;

/**
 * Created by Claudio on 21.05.2017.
 */
public abstract class JSONHandler {
    private static final Gson gson = new Gson();

    private static String buildJson(int type, JsonElement data) {
        JsonObject message = new JsonObject();
        message.addProperty("type", type);
        message.add("data", data);
        return gson.toJson(message);
    }

    private static JsonObject buildToken(Token token) {
        JsonObject t = new JsonObject();
        t.addProperty("x", token.getU());
        t.addProperty("y", token.getV());
        return t;
    }

    private static JsonArray buildTokenArray(Token[] tokens) {
        JsonArray array = new JsonArray();
        for (Token token : tokens) {
            array.add(buildToken(token));
        }
        return array;
    }

    private static JsonObject buildPlaced(int[] placed) {
        JsonObject t = new JsonObject();
        t.addProperty("white", placed[0]);
        t.addProperty("black", placed[1]);
        return t;
    }

    // server to client

    /**
     * Generates a new init json message
     * @param player color of player
     * @param placed {white, black} current placed tokens
     * @param selection selectable tokens
     * @return json as string
     */
    public static String buildJsonInit(Token.Color player, int[] placed, Token[] selection) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("placed", buildPlaced(placed));
        data.add("selection", buildTokenArray(selection));
        return buildJson(JSONMessage.SERVER_INIT, data);
    }

    /**
     * Generates a new place json message for the actor
     * @param player color of actor
     * @param placed {white, black} current placed tokens
     * @param source new placed token
     * @param changes all tokens who has changed
     * @return json as string
     */
    public static String buildJSONPlaceClient(Token.Color player, int[] placed, Token source, Token[] changes) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("placed", buildPlaced(placed));
        data.add("source", buildToken(source));
        data.add("changes", buildTokenArray(changes));
        return buildJson(JSONMessage.SERVER_PLACE_CLIENT, data);
    }

    /**
     * Generates a new place json message for the opponent of current action
     * @param player color of actor
     * @param placed {white, black} current placed tokens
     * @param source new placed token
     * @param changes all tokens who has changed
     * @param selection selectable tokens for the opponent of current action
     * @param pass can send auto pass action by client
     * @return json as string
     */
    public static String buildJSONPlaceOpponent(Token.Color player, int[] placed, Token source, Token[] changes, Token[] selection, int pass) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("placed", buildPlaced(placed));
        data.add("source", buildToken(source));
        data.add("changes", buildTokenArray(changes));
        data.add("selection", buildTokenArray(selection));
        data.addProperty("pass", pass);
        return buildJson(JSONMessage.SERVER_PLACE_OPPONENT, data);
    }

    /**
     * Generates a new pass json message for the actor
     * @param player color of actor
     * @return json as string
     */
    public static String buildJSONPassClient(Token.Color player) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        return buildJson(JSONMessage.SERVER_PASS_CLIENT, data);
    }

    /**
     * Generates a new pass json message for the opponent of current action
     * @param player color of actor
     * @param selection selectable tokens for the opponent of current action
     * @param pass can send auto pass action by client
     * @return json as string
     */
    public static String buildJSONPassOpponent(Token.Color player, Token[] selection, int pass) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("selection", buildTokenArray(selection));
        data.addProperty("pass", pass);
        return buildJson(JSONMessage.SERVER_PASS_OPPONENT, data);
    }

    /**
     * Generates a new end message for a player
     * @param win 1 victory, 0 remis, -1 defeat
     * @return json as string
     */
    public static String buildJSONEnd(int win) {
        JsonObject data = new JsonObject();
        data.addProperty("win", win);
        return buildJson(JSONMessage.SERVER_END, data);
    }

    /**
     * Generates a new error message for a player
     * @param error error code
     * @return json as string
     */
    public static String buildJSONError(int error) {
        JsonObject data = new JsonObject();
        data.addProperty("error", error);
        return buildJson(JSONMessage.SERVER_ERROR, data);
    }

    // client to server

    /**
     * Gets the x and y of a message
     * @param object data of client message
     * @return {x, y} of selected token
     */
    public static int[] getXYfromJSON(JsonObject object) {
        return new int[] {object.get("x").getAsInt(), object.get("y").getAsInt()};
    }

    /**
     * Gets the color and game type of a message
     * @param object data of client message
     * @return {color, gameType} of selected token
     */
    public static int[] getColorTypefromJSON(JsonObject object) {
        return new int[] {object.get("color").getAsInt(), object.get("gameType").getAsInt()};
    }
}
