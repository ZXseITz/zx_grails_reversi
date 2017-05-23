package reversi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import reversi.actions.Action;
import reversi.actions.ChangedAction;
import reversi.actions.PassAction;
import reversi.actions.PlacingAction;
import reversi.bot.Bot;
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
import java.util.concurrent.Future;

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
                    Player player = users.get(client.getId());
                    Token.Color pColor = Token.Color.WHITE; /* Math.random() < 0.5? Token.Color.WHITE: Token.Color.BLACK */
                    Board board = new Board(new BoardModel());
                    board.setUpBoard();
                    Bot bot = new Bot(Token.getOpposite(pColor));
                    Round r = new Round(player, board, pColor, bot);
//                    rounds.putIfAbsent(r.getId(), r);
                    users.get(client.getId()).setRound(r);

                    if (r.getPlayerColor() == Token.Color.WHITE) {
                        List<Token> list = r.getBoard().getSelectableTokens(r.getPlayerColor());
                        Token[] selection = new Token[list.size()];
                        list.toArray(selection);
                        String json = JSONHandler.buildJsonSelection(r.getPlayerColor(), selection);
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
                        PlacingAction a = new PlacingAction(r.getPlayerColor(), xy[0], xy[1]);
                        ChangedAction changed = board.submit(a);
                        if (changed != null) {
                            String json = JSONHandler.buildJSONPlace(changed.getPlayer(), changed.getSource(), changed.getNeighbours());
                            p.send(json);

                            respond(r);
                        }
                    }
                    break;
                }
                case JSONMessage.CLIENT_PASS:
                    Player p = users.get(client.getId());
                    if (p.isInRound()) {
                        Round r = p.getRound();
                        Board board = r.getBoard();
                        PassAction a = new PassAction(r.getPlayerColor());
                        ChangedAction changed = board.submit(a);
                        if (changed != null) {
                            String json = JSONHandler.buildJSONPass(r.getPlayerColor());
                            p.send(json);

                            respond(r);
                        }
                    }
                    break;
            }
        } catch (Exception e) {

        }
    }

    private void respond(Round r) {
        Board board = r.getBoard();
        if (board.isFinished()) {
            int win = board.winner(r.getPlayerColor());
            String json = JSONHandler.buildJSONEnd(win);
            r.getPlayer().send(json);
        } else {
            try {
                Future<Action> reaction = r.getBot().submit(board);
                botAction(reaction.get(), r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void botAction (Action action, Round r) {
        Board board = r.getBoard();
        if (action instanceof PlacingAction) {
            ChangedAction changed = board.submit(action);
            if (changed != null) {
                List<Token> list = r.getBoard().getSelectableTokens(r.getPlayerColor());
                Token[] selection = new Token[list.size()];
                list.toArray(selection);
                String json = JSONHandler.buildJSONPlaceSelect(changed.getPlayer(), changed.getSource(), changed.getNeighbours(), selection);
                r.getPlayer().send(json);
            }
        } else if (action instanceof PassAction) {
            ChangedAction changed = board.submit(action);
            if (changed != null) {
                List<Token> list = r.getBoard().getSelectableTokens(r.getPlayerColor());
                Token[] selection = new Token[list.size()];
                list.toArray(selection);
                String json = JSONHandler.buildJSONPassSelect(changed.getPlayer(), selection);
                r.getPlayer().send(json);
            }
        }
    }
}
