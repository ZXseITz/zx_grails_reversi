package reversi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import reversi.actions.PlacingAction;
import reversi.actions.SelectionAction;
import reversi.game.*;
import reversi.json.JSONHandler;
import reversi.json.JSONMessage;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;

/**
 * Created by Claudio on 17.05.2017.
 */
@WebListener
@ServerEndpoint("/reversi/room")
public class ReversiServer implements ServletContextListener {
    private Map<String, Player> users;
//    private Map<Integer, Round> rounds;

    public ReversiServer() {
        users = new ConcurrentHashMap<>();
//        rounds = new ConcurrentHashMap<>();
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
            JsonObject o = new JsonParser().parse(message).getAsJsonObject();
            int type = o.get("type").getAsInt();
            switch (type) {
                case JSONMessage.CLIENT_NEW_BOT_GAME: {
                    Round r = new Round(users.get(client.getId()), new Board(new BoardModel()), Token.Color.WHITE /* Math.random() < 0.5? Token.Color.WHITE: Token.Color.BLACK */);
//                    rounds.putIfAbsent(r.getId(), r);
                    users.get(client.getId()).setRound(r);

                    if (r.getPlayerColor() == Token.Color.WHITE) {
                        List<Token> list = r.getBoard().getSelectableTokens(r.getPlayerColor());
                        Token[] selection = new Token[list.size()];
                        list.toArray(selection);
                        SelectionAction action = new SelectionAction(r.getPlayerColor(), selection);
                        String json = JSONHandler.buildJsonSelection(action);
                        client.getBasicRemote().sendText(json);
                    }

//                    System.out.println("send to Player " + client.getId() + " json " + json);
                    break;
                }
                case JSONMessage.CLIENT_NEW_GAME:
                    //TODO implement pvp
                    break;
                case JSONMessage.CLIENT_PLACE: {
                    Player p = users.get(client.getId());
                    if (p.isInRound()) {
                        Round r = p.getRound();
                        Board board = r.getBoard();
                        int[] xy = JSONHandler.getXYfromJSON(o.getAsJsonObject("data"));
                        Token source = board.get(xy[0], xy[1]);
                        List<Token> toChange = board.detectNeighbours(source, r.getPlayerColor());
                        if (toChange.size() > 0) {
                            Token[] changes = new Token[toChange.size()];
                            toChange.toArray(changes);
                            PlacingAction action = new PlacingAction(r.getPlayerColor(), source, changes);
                            String json = JSONHandler.buildJSONPlace(action);
                            p.send(json);

                            //TODO send action to bot
                        }
                    }
                    break;
                }
                case JSONMessage.CLIENT_PASS:

                    break;
            }
        } catch (Exception e) {
        }
    }
}
