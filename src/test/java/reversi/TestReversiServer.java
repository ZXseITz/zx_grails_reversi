package reversi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import reversi.bot.RoundBot;
import reversi.game.Player;
import reversi.game.Round;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;
import reversi.pvp.RoundPVP;

import javax.websocket.Session;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestReversiServer {
    private Session client;
    private Player player;
    private ReversiServer server;

    @Before
    public void setup() {
        String id = "0";

        client = Mockito.mock(Session.class);
        Mockito.when(client.getId()).thenReturn(id);

        player = Mockito.mock(Player.class);
        Mockito.when(player.getID()).thenReturn(id);
        Mockito.when(player.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setState(Mockito.any(Player.State.class));
        Mockito.when(player.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setRound(Mockito.any(Round.class));

        server = new ReversiServer();
    }

    @Test
    public void testOnOpen() {
        server.onOpen(client);
        Assert.assertEquals(1, server.getUsers().size());
        Player newPlayer = server.getUsers().get(client.getId());
        Assert.assertEquals(client.getId(), newPlayer.getID());
        Assert.assertEquals(Player.State.ONLINE, newPlayer.getState());
    }

    @Test
    public void testOnClose() {
        server.getUsers().putIfAbsent(player.getID(), player);

        player.setState(Player.State.ONLINE);
        server.onClose(client);
        Assert.assertEquals(0, server.getUsers().size());
        Assert.assertEquals(Player.State.OFFLINE, player.getState());
    }

    @Test
    public void testOnCloseIngame() {
        server.getUsers().putIfAbsent(player.getID(), player);
        RoundPVP round = Mockito.mock(RoundPVP.class);
        Mockito.doNothing().when(round).disconnect(player);
        player.setRound(round);

        player.setState(Player.State.INGAME);
        server.onClose(client);
        Assert.assertEquals(0, server.getUsers().size());
        Assert.assertEquals(Player.State.OFFLINE, player.getState());
        Mockito.verify(round).disconnect(player);
    }

    @Test
    public void testOnMessageNewBotgame() {
        Mockito.doNothing().when(player).send(Mockito.anyString());
        server.getUsers().putIfAbsent(player.getID(), player);
        player.setState(Player.State.ONLINE);
        player.setRound(null);
        String message = "{\"type\":0,\"data\":{\"color\":1,\"gameType\":0}}";

        server.onMessage(message, client);
        Assert.assertTrue(player.getRound() instanceof RoundBot);
        Assert.assertEquals(Player.State.INGAME, player.getState());
        Mockito.verify(player).send(Mockito.anyString());
    }

    @Test
    public void testOnMessageNewgameInvalid() {
        Mockito.doNothing().when(player).send(Mockito.anyString());
        server.getUsers().putIfAbsent(player.getID(), player);
        player.setState(Player.State.ONLINE);
        player.setRound(null);
        String message = "{\"type\":0,\"data\":{\"color\":0,\"gameType\":0}}";

        server.onMessage(message, client);
        Assert.assertNull(player.getRound());
        Assert.assertEquals(Player.State.ONLINE, player.getState());
        Mockito.verify(player, Mockito.never()).send(Mockito.anyString());
    }



    @Test
    public void testOnMessagePlaceValid() {
        server.getUsers().putIfAbsent(player.getID(), player);
        RoundPVP round = Mockito.mock(RoundPVP.class);
        Mockito.doNothing().when(round).place(Mockito.eq(player), Mockito.any(int[].class));
        player.setRound(round);
        player.setState(Player.State.INGAME);
        String message = "{\"type\":10,\"data\":{\"x\":4,\"y\":2}}";

        server.onMessage(message, client);
        Mockito.verify(round).place(Mockito.eq(player), Mockito.any(int[].class));
    }

    @Test
    public void testOnMessagePlaceInvalid() {
        server.getUsers().putIfAbsent(player.getID(), player);
        Mockito.doNothing().when(player).send(Mockito.anyString());
        player.setState(Player.State.ONLINE);
        String message = "{\"type\":10,\"data\":{\"x\":4,\"y\":2}}";

        server.onMessage(message, client);
        Mockito.verify(player).send(JSONHandler.buildJSONError(JSONMessage.Error.INVALID_GAME));
    }

    @Test
    public void testOnMessagePassValid() {
        server.getUsers().putIfAbsent(player.getID(), player);
        RoundPVP round = Mockito.mock(RoundPVP.class);
        Mockito.doNothing().when(round).pass(Mockito.eq(player));
        player.setRound(round);
        player.setState(Player.State.INGAME);
        String message = "{\"type\":20}";

        server.onMessage(message, client);
        Mockito.verify(round).pass(Mockito.eq(player));
    }

    @Test
    public void testOnMessagePassInvalid() {
        server.getUsers().putIfAbsent(player.getID(), player);
        player.setState(Player.State.ONLINE);
        String message = "{\"type\":20}";

        server.onMessage(message, client);
        Mockito.verify(player).send(JSONHandler.buildJSONError(JSONMessage.Error.INVALID_GAME));
    }
}
