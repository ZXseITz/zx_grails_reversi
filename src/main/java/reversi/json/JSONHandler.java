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

    public static String buildJsonInit(Token.Color player, int[] placed, Token[] selection) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("placed", buildPlaced(placed));
        data.add("selection", buildTokenArray(selection));
        return buildJson(JSONMessage.SERVER_INIT, data);
    }
    
    public static String buildJSONPlaceClient(Token.Color player, int[] placed, Token source, Token[] changes) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("placed", buildPlaced(placed));
        data.add("source", buildToken(source));
        data.add("changes", buildTokenArray(changes));
        return buildJson(JSONMessage.SERVER_PLACE_CLIENT, data);
    }

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

    public static String buildJSONPassClient(Token.Color player) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        return buildJson(JSONMessage.SERVER_PASS_CLIENT, data);
    }

    public static String buildJSONPassOpponent(Token.Color player, Token[] selection, int pass) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("selection", buildTokenArray(selection));
        data.addProperty("pass", pass);
        return buildJson(JSONMessage.SERVER_PASS_OPPONENT, data);
    }

    public static String buildJSONEnd(int win) {
        JsonObject data = new JsonObject();
        data.addProperty("win", win);
        return buildJson(JSONMessage.SERVER_END, data);
    }

    // client to server

    public static int[] getXYfromJSON(JsonObject object) {
        return new int[]{object.get("x").getAsInt(), object.get("y").getAsInt()};
    }
}
