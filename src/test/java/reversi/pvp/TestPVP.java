package reversi.pvp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;

/**
 * Created by Claudio on 27.05.2017.
 */
public class TestPVP {
    private Player player, player2;
    private PVP pvp;

    @Before
    public void setup() {
        player = Mockito.mock(Player.class);
        Mockito.when(player.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setState(Mockito.any(Player.State.class));
        Mockito.when(player.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player).setRound(Mockito.any(Round.class));

        player2 = Mockito.mock(Player.class);
        Mockito.when(player2.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player2).setState(Mockito.any(Player.State.class));
        Mockito.when(player2.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player2).setRound(Mockito.any(Round.class));

        pvp = new PVP();
    }

    @Test
    public void testWaitForMatchingOnline() {
        player.setState(Player.State.ONLINE);

        Assert.assertTrue(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertEquals(Player.State.WAITING, player.getState());
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.BLACK));
    }

    @Test
    public void testWaitForMatchingIngameBot() {
        player.setState(Player.State.INGAME);

        Assert.assertTrue(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertEquals(Player.State.WAITING, player.getState());
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.BLACK));
    }

    @Test
    public void testWaitForMatchingIngamePvp() {
        player.setState(Player.State.INGAME);
        player2.setState(Player.State.INGAME);
        Mockito.doNothing().when(player2).send(Mockito.anyString());

        RoundPVP round = Mockito.mock(RoundPVP.class);
        Mockito.when(round.getOpponent(player)).thenReturn(player2);
        Mockito.doCallRealMethod().when(round).disconnect(Mockito.any(Player.class));

        player.setRound(round);
        player2.setRound(round);

        Assert.assertTrue(pvp.waitForMatching(player, Token.Color.BLACK));
        Assert.assertEquals(Player.State.WAITING, player.getState());
        Assert.assertEquals(Player.State.ONLINE, player2.getState());
        Assert.assertNull(player2.getRound());
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.BLACK));
    }
}
