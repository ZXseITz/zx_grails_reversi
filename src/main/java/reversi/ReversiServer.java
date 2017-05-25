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
                case JSONMessage.CLIENT_NEW_GAME: {
                    Player player = users.get(client.getId());
                    int[] ct = JSONHandler.getColorTypefromJSON(o.getAsJsonObject("data"));
                    Token.Color clientColor = Token.getColorFromValue(ct[0]);
                    if (clientColor != Token.Color.UNDEFINED &&
                            (ct[1] == JSONMessage.GameType.BOT || ct[1] == JSONMessage.GameType.PVP)) {
                        Board board = new Board();
                        board.setUpBoard();

                        if (ct[1] == JSONMessage.GameType.BOT) {
                            //bot game
                            Bot bot = new Bot(Token.getOpposite(clientColor));
                            Round r = new Round(player, board, clientColor, bot);
                            users.get(client.getId()).setRound(r);
                            if (clientColor == Token.Color.WHITE) {
                                //client starts
                                Token[] selection = getSelection(board, clientColor);
                                String json = JSONHandler.buildJsonInit(clientColor, board.getPlacedTokens(), selection);
                                client.getBasicRemote().sendText(json);
                            } else {
                                //bot starts
                                respond(r);
                            }
                        } else {
                            //pvp

                        }
                    }

//                    rounds.putIfAbsent(r.getId(), r);
//                    System.out.println("send to Player " + client.getId() + " json " + json);
                    break;
                }
                case JSONMessage.CLIENT_PLACE: {
                    Player p = users.get(client.getId());
                    if (p.isInRound()) {
                        Round r = p.getRound();
                        Board board = r.getBoard();
                        int[] xy = JSONHandler.getXYfromJSON(o.getAsJsonObject("data"));
                        PlacingAction a = new PlacingAction(r.getPlayerColor(), xy[0], xy[1]);
                        ChangedAction changed = board.submit(a);
                        if (changed != null) {
                            String json = JSONHandler.buildJSONPlaceClient(changed.getPlayer(), board.getPlacedTokens(),
                                    changed.getSource(), changed.getNeighbours());
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
                            String json = JSONHandler.buildJSONPassClient(r.getPlayerColor());
                            p.send(json);

                            respond(r);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void respond(Round r) {
        Board board = r.getBoard();
        if (board.isFinished()) {
            sendEnding(r);
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
                Token[] selection = getSelection(board, r.getPlayerColor());
                String json = JSONHandler.buildJSONPlaceOpponent(changed.getPlayer(), board.getPlacedTokens(),
                        changed.getSource(), changed.getNeighbours(), selection, !board.isFinished() ? 1 : 0);
                r.getPlayer().send(json);

                if (board.isFinished()) {
                    sendEnding(r);
                }
            }
        } else if (action instanceof PassAction) {
            ChangedAction changed = board.submit(action);
            if (changed != null) {
                Token[] selection = getSelection(board, r.getPlayerColor());
                String json = JSONHandler.buildJSONPassOpponent(changed.getPlayer(), selection,
                        !board.isFinished() ? 1 : 0);
                r.getPlayer().send(json);

                if (board.isFinished()) {
                    sendEnding(r);
                }
            }
        }
    }

    private Token[] getSelection(Board board, Token.Color color) {
        List<Token> list = board.getSelectableTokens(color);
        Token[] selection = new Token[list.size()];
        list.toArray(selection);
        return selection;
    }

    private void sendEnding(Round r) {
        int win = r.getBoard().winner(r.getPlayerColor());
        String json2 = JSONHandler.buildJSONEnd(win);
        r.getPlayer().send(json2);
    }
}
