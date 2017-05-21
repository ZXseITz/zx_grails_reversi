package reversi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public static String buildJsonServerInit(Token.Color color, Token[] selectables) {
        JsonObject o = new JsonObject();
        o.addProperty("color", color.getValue());
        JsonArray selects = new JsonArray();
        for (Token token : selectables) {
            selects.add(buildToken(token));
        }
        o.add("selectables", selects);
        return buildJson(JSONMessage.SERVER_INIT, o);
    }
}
