package reversi.pvp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import reversi.game.Player;
import reversi.game.Round;
import reversi.game.Token;

import static java.lang.Thread.sleep;

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
        Mockito.doNothing().when(player).send(Mockito.anyString());

        player2 = Mockito.mock(Player.class);
        Mockito.when(player2.getState()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player2).setState(Mockito.any(Player.State.class));
        Mockito.when(player2.getRound()).thenCallRealMethod();
        Mockito.doCallRealMethod().when(player2).setRound(Mockito.any(Round.class));
        Mockito.doNothing().when(player2).send(Mockito.anyString());

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
    public void testWaitForMatchingWaiting() {
        player.setState(Player.State.WAITING);

        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertEquals(Player.State.WAITING, player.getState());
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertFalse(pvp.waitForMatching(player, Token.Color.BLACK));
    }

    @Test
    public void testMatchingOnline() {
        player.setState(Player.State.ONLINE);
        player2.setState(Player.State.ONLINE);

        Assert.assertTrue(pvp.waitForMatching(player, Token.Color.WHITE));
        Assert.assertTrue(pvp.waitForMatching(player2, Token.Color.BLACK));

        /*
        wait for matching thread
        test can fail if sleep is too short
          */
        try {
            sleep(500);
        } catch (InterruptedException e) {
            //ignore
        }

        // check other thread
        Assert.assertEquals(Player.State.INGAME, player.getState());
        Assert.assertEquals(Player.State.INGAME, player2.getState());
        Assert.assertTrue(player.getRound() instanceof RoundPVP);
        Assert.assertSame(player.getRound(), player2.getRound());
        // check if round.start() was called
        Mockito.verify(player).send(Mockito.anyString());
        Mockito.verify(player2).send(Mockito.anyString());
    }

    @Test
    public void testMatchingOffline() {
        player.setState(Player.State.ONLINE);
        player.setRound(null);
        player2.setState(Player.State.ONLINE);
        player2.setRound(null);

        Assert.assertTrue(pvp.waitForMatching(player, Token.Color.WHITE));
        player.setState(Player.State.OFFLINE);
        Assert.assertTrue(pvp.waitForMatching(player2, Token.Color.BLACK));

        /*
        wait for matching thread
        test can fail if sleep is too short
         */
        try {
            sleep(500);
        } catch (InterruptedException e) {
            //ignore
        }

        // check other thread
        Assert.assertEquals(Player.State.OFFLINE, player.getState());
        Assert.assertEquals(Player.State.WAITING, player2.getState());
        Assert.assertNull(player.getRound());
        Assert.assertNull(player.getRound());
        // check if round.start() was not called
        Mockito.verify(player, Mockito.never()).send(Mockito.anyString());
        Mockito.verify(player2, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    public void testMatchingIngame() {
        player.setState(Player.State.ONLINE);
        player.setRound(null);
        player2.setState(Player.State.ONLINE);
        player2.setRound(null);

        Assert.assertTrue(pvp.waitForMatching(player, Token.Color.WHITE));
        player.setState(Player.State.INGAME);
        Assert.assertTrue(pvp.waitForMatching(player2, Token.Color.BLACK));

        try {
            sleep(200);
        } catch (InterruptedException e) {
            //ignore
        }

        // check other thread
        Assert.assertEquals(Player.State.INGAME, player.getState());
        Assert.assertEquals(Player.State.WAITING, player2.getState());
        Assert.assertNull(player.getRound());
        Assert.assertNull(player.getRound());
        // check if round.start() was not called
        Mockito.verify(player, Mockito.never()).send(Mockito.anyString());
        Mockito.verify(player2, Mockito.never()).send(Mockito.anyString());
    }
}
