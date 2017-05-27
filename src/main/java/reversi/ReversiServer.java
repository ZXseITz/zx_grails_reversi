package reversi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mvc.ReversiEndPointConfig;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import reversi.bot.Bot;
import reversi.bot.RoundBot;
import reversi.game.*;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;
import reversi.pvp.PVP;
import reversi.pvp.RoundPVP;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

/**
 * Created by Claudio on 17.05.2017.
 */
@WebListener
@ServerEndpoint(value = "/reversi/room", configurator = ReversiEndPointConfig.class)
public class ReversiServer implements ServletContextListener {
    private final JsonParser gParser;
    private final Map<String, Player> users;
    private final PVP pvp;

    /**
     * Constructor for production code
     * Cannot have any parameters, reason: grails application, configurator
     */
    public ReversiServer() {
        gParser = new JsonParser();
        users = new ConcurrentHashMap<>();
        this.pvp = new PVP();
    }

    /**
     * Constructor for mock testing only
     */
    ReversiServer(PVP pvp) {
        gParser = new JsonParser();
        users = new ConcurrentHashMap<>();
        this.pvp = pvp;
    }

    public Map<String, Player> getUsers() {
        return users;
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

    /**
     * Adds the client as a player to users
     * @param client joined client
     */
    @OnOpen
    public void onOpen(Session client) {
        Player player = new Player(client);
        synchronized (player) {
            player.setState(Player.State.ONLINE);
        }
        users.putIfAbsent(client.getId(), player);
        System.out.println("Player " + client.getId() + " has joined");
    }

    /**
     * Disconnects the player and removes him from current round
     * Sets player to offline, if he is in pvp queue
     * @param client left client
     */
    @OnClose
    public void onClose(Session client) {
        Player player = users.get(client.getId());
        synchronized (player) {
            if (player.getState() == Player.State.INGAME) {
                Round round = player.getRound();
                if(round instanceof RoundPVP) {
                    ((RoundPVP) round).disconnect(player);
                }
            }
            player.setState(Player.State.OFFLINE); //mark for pvp matcher
        }
        users.remove(client.getId());
        System.out.println("Player " + client.getId() + " has left");
    }

    @OnError
    public void onError(Throwable t) {

    }

    /**
     * Handles the json messages from the client
     * @param message json message
     * @param client sender
     */
    @OnMessage
    public void onMessage(String message, Session client) {
        try {
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
                            synchronized (player) {
                                Board board = new Board();
                                board.setUpBoard();
                                Bot bot = new Bot(Token.getOpposite(playerColor));
                                Round round = new RoundBot(board, player, playerColor, bot);
                                player.setRound(round);
                                player.setState(Player.State.INGAME);
                                round.start();
                            }
                        } else {
                            //pvp
                            pvp.waitForMatching(player, playerColor);
                        }
                    }
                    break;
                }
                case JSONMessage.CLIENT_PLACE: {
                    Player player = users.get(client.getId());
                    int[] xy = JSONHandler.getXYfromJSON(json.getAsJsonObject("data"));
                    if (player.getState() != Player.State.INGAME) {
                        String error = JSONHandler.buildJSONError(JSONMessage.Error.INVALID_GAME);
                        player.send(error);
                    } else {
                        player.getRound().place(player, xy);
                    }
                    break;
                }
                case JSONMessage.CLIENT_PASS: {
                    Player player = users.get(client.getId());
                    if (player.getState() != Player.State.INGAME) {
                        String error = JSONHandler.buildJSONError(JSONMessage.Error.INVALID_GAME);
                        player.send(error);
                    } else {
                        player.getRound().pass(player);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
