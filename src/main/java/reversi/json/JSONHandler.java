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

    // server to client

    public static String buildJsonSelection(Token.Color player, Token[] selection) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("selection", buildTokenArray(selection));
        return buildJson(JSONMessage.SERVER_INIT, data);
    }
    
    public static String buildJSONPlace(Token.Color player, Token source, Token[] changes) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("source", buildToken(source));
        data.add("changes", buildTokenArray(changes));
        return buildJson(JSONMessage.SERVER_PLACE_CLIENT, data);
    }

    public static String buildJSONPlaceSelect(Token.Color player, Token source, Token[] changes, Token[] selection) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("source", buildToken(source));
        data.add("changes", buildTokenArray(changes));
        data.add("selection", buildTokenArray(selection));
        return buildJson(JSONMessage.SERVER_PLACE_OPPONENT, data);
    }

    public static String buildJSONPass(Token.Color player) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        return buildJson(JSONMessage.SERVER_PASS_CLIENT, data);
    }

    public static String buildJSONPassSelect(Token.Color player, Token[] selection) {
        JsonObject data = new JsonObject();
        data.addProperty("color", player.getValue());
        data.add("selection", buildTokenArray(selection));
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
