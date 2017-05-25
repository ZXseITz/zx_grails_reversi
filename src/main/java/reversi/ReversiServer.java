package reversi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import reversi.bot.Bot;
import reversi.bot.RoundBot;
import reversi.game.*;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;
import reversi.pvp.RoundPVP;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Claudio on 17.05.2017.
 */
@WebListener
@ServerEndpoint("/reversi/room")
public class ReversiServer implements ServletContextListener {
    private JsonParser gParser;
    private Map<String, Player> users;
    private final BlockingQueue<Player> pvpWhite;
    private final BlockingQueue<Player> pvpBlack;
//    private Map<Integer, Round> rounds;

    public ReversiServer() {
        gParser = new JsonParser();
        users = new ConcurrentHashMap<>();
//        rounds = new ConcurrentHashMap<>();
        pvpWhite = new ArrayBlockingQueue<>(20);
        pvpBlack = new ArrayBlockingQueue<>(20);
        Thread matcher = new Thread(() -> {
            try {
                while (true) {
                    startPVP(pvpWhite.take(), pvpBlack.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        matcher.start();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        final ServerContainer serverContainer = (ServerContainer) servletContext
                .getAttribute("javax.websocket.server.ServerContainer");
        try {
            serverContainer.addEndpoint(ReversiServer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @OnOpen
    public void onOpen(Session client) {
        users.putIfAbsent(client.getId(), new Player(client));
        System.out.println("Player " + client.getId() + " has joined");
    }

    @OnClose
    public void onClose(Session client) {
//        Player p = users.get(client.getId());
//        if (p.isInRound()) rounds.remove(p.getRound().getId());
        users.remove(client.getId());
        System.out.println("Player " + client.getId() + " has left");
    }

    @OnError
    public void onError(Throwable t) {

    }

    @OnMessage
    public void onMessage(String message, Session client) {
        try {
//            System.out.println("Player " + client.getId() + " send:" + message);
            JsonObject json = gParser.parse(message).getAsJsonObject();
            int type = json.get("type").getAsInt();
            switch (type) {
                case JSONMessage.CLIENT_NEW_GAME: {
                    Player player = users.get(client.getId());
                    int[] ct = JSONHandler.getColorTypefromJSON(json.getAsJsonObject("data"));
                    Token.Color playerColor = Token.getColorFromValue(ct[0]);
                    if (playerColor != Token.Color.UNDEFINED &&
                            (ct[1] == JSONMessage.GameType.BOT || ct[1] == JSONMessage.GameType.PVP)) {
                        if (ct[1] == JSONMessage.GameType.BOT) {
                            //bot game
                            Board board = new Board();
                            board.setUpBoard();
                            Bot bot = new Bot(Token.getOpposite(playerColor));
                            Round round = new RoundBot(board, player, playerColor, bot);
                            round.start();
                        } else {
                            //pvp
                            if (playerColor == Token.Color.WHITE) pvpWhite.add(player);
                            else pvpBlack.add(player);
                        }
                    }
//                    rounds.putIfAbsent(r.getId(), r);
//                    System.out.println("send to Player " + client.getId() + " json " + json);
                    break;
                }
                case JSONMessage.CLIENT_PLACE: {
                    Player player = users.get(client.getId());
                    int[] xy = JSONHandler.getXYfromJSON(json.getAsJsonObject("data"));
                    player.getRound().place(player, xy);
                    break;
                }
                case JSONMessage.CLIENT_PASS: {
                    Player player = users.get(client.getId());
                    player.getRound().pass(player);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPVP(Player white, Player black) {
        Board board = new Board();
        board.setUpBoard();
        Round round = new RoundPVP(board, white, black);
        round.start();
    }
}
