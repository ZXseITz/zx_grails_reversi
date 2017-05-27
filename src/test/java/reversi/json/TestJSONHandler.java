package reversi.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import reversi.game.Token;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestJSONHandler {
    @Test
    public void testBuildJsonInit() {
        String e = "{\"type\":2,\"data\":{\"color\":1,\"placed\":{\"white\":2,\"black\":2},\"selection\":[{\"x\":4,\"y\":2},{\"x\":5,\"y\":3},{\"x\":2,\"y\":2},{\"x\":3,\"y\":5}]}}";
        Assert.assertEquals(e, JSONHandler.buildJsonInit(Token.Color.WHITE, new int[]{2, 2}, new Token[]
                {new Token(4, 2), new Token(5, 3), new Token(2, 2), new Token(3, 5)}));
    }

    @Test
    public void testBuildJSONError() {
        String ig = "{\"type\":5,\"data\":{\"error\":10}}";
        Assert.assertEquals(ig, JSONHandler.buildJSONError(JSONMessage.Error.INVALID_GAME));
        String ia = "{\"type\":5,\"data\":{\"error\":11}}";
        Assert.assertEquals(ia, JSONHandler.buildJSONError(JSONMessage.Error.INVALID_ACTION));
        String od = "{\"type\":5,\"data\":{\"error\":20}}";
        Assert.assertEquals(od, JSONHandler.buildJSONError(JSONMessage.Error.OPPONENT_DISCONNECTED));
    }

    @Test
    public void testBuildJSONPlaceClient() {
        String e = "{\"type\":11,\"data\":{\"color\":1,\"placed\":{\"white\":4,\"black\":1},\"source\":{\"x\":4,\"y\":2},\"changes\":[{\"x\":4,\"y\":3}]}}";
        Token source = new Token(4, 2);
        source.setColor(Token.Color.WHITE);
        Token change = new Token(4, 3);
        change.setColor(Token.Color.BLACK);
        Assert.assertEquals(e, JSONHandler.buildJSONPlaceClient(Token.Color.WHITE, new int[] {4, 1}, source,
                new Token[] {change}));
    }

    @Test
    public void testBuildJSONPlaceOpponent() {
        String e = "{\"type\":12,\"data\":{\"color\":1,\"placed\":{\"white\":4,\"black\":1},\"source\":{\"x\":4,\"y\":2},\"changes\":[{\"x\":4,\"y\":3}],\"selection\":[{\"x\":3,\"y\":2},{\"x\":5,\"y\":2},{\"x\":5,\"y\":4}],\"pass\":1}}";
        Token source = new Token(4, 2);
        source.setColor(Token.Color.WHITE);
        Token change = new Token(4, 3);
        change.setColor(Token.Color.BLACK);
        Assert.assertEquals(e, JSONHandler.buildJSONPlaceOpponent(Token.Color.WHITE, new int[] {4, 1}, source,
                new Token[] {change}, new Token[] {new Token(3, 2), new Token(5, 2), new Token(5, 4)},
                1));
    }

    @Test
    public void testBuildJSONPassClient() {
        String e = "{\"type\":21,\"data\":{\"color\":1}}";
        Assert.assertEquals(e, JSONHandler.buildJSONPassClient(Token.Color.WHITE));
    }

    @Test
    public void testBuildJSONPassOpponent() {
        String e = "{\"type\":22,\"data\":{\"color\":1,\"selection\":[{\"x\":3,\"y\":2},{\"x\":5,\"y\":2},{\"x\":5,\"y\":4}],\"pass\":1}}";
        Assert.assertEquals(e, JSONHandler.buildJSONPassOpponent(Token.Color.WHITE,
                new Token[] {new Token(3, 2), new Token(5, 2), new Token(5, 4)}, 1));
    }

    @Test
    public void testBuildJSONEnd() {
        String ew = "{\"type\":50,\"data\":{\"win\":1}}";
        Assert.assertEquals(ew, JSONHandler.buildJSONEnd(1));
        String er = "{\"type\":50,\"data\":{\"win\":0}}";
        Assert.assertEquals(er, JSONHandler.buildJSONEnd(0));
        String el = "{\"type\":50,\"data\":{\"win\":-1}}";
        Assert.assertEquals(el, JSONHandler.buildJSONEnd(-1));
    }

    @Test
    public void testGetColorTypefromJSON() {
        String json = "{\"type\":0,\"data\":{\"color\":1,\"gameType\":0}}";
        JsonObject o = new JsonParser().parse(json).getAsJsonObject();
        Assert.assertArrayEquals(new int[] {1, 0}, JSONHandler.getColorTypefromJSON(o.getAsJsonObject("data")));
    }

    @Test
    public void testGetXYfromJSON() {
        String json = "{\"type\":10,\"data\":{\"x\":4,\"y\":2}}";
        JsonObject o = new JsonParser().parse(json).getAsJsonObject();
        Assert.assertArrayEquals(new int[] {4, 2}, JSONHandler.getXYfromJSON(o.getAsJsonObject("data")));
    }
}
