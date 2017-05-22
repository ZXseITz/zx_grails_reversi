package reversi.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import reversi.game.Token;
import reversi.actions.PlacingAction;
import reversi.actions.SelectionAction;

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

    public static String buildJsonSelection(SelectionAction action) {
        JsonObject data = new JsonObject();
        data.addProperty("color", action.getPlayer().getValue());
        data.add("selection", buildTokenArray(action.getSelection()));
        return buildJson(JSONMessage.SERVER_INIT, data);
    }
    
    public static String buildJSONPlace(PlacingAction action) {
        JsonObject data = new JsonObject();
        data.addProperty("color", action.getPlayer().getValue());
        data.add("source", buildToken(action.getSource()));
        data.add("changes", buildTokenArray(action.getToChange()));
        return buildJson(JSONMessage.SERVER_PLACE_CLIENT, data);
    }

    // client to server

    public static int[] getXYfromJSON(JsonObject object) {
        return new int[]{object.get("x").getAsInt(), object.get("y").getAsInt()};
    }
}
