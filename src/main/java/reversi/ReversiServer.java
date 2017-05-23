package reversi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import reversi.actions.Action;
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
                        Token source = board.get(xy[0], xy[1]);
                        List<Token> list = board.detectNeighbours(source, r.getPlayerColor());
                        PlacingAction a = new PlacingAction(r.getPlayerColor(), source);
                        if (r.getBoard().submit(a)) {
                            Token[] changes = new Token[list.size()];
                            list.toArray(changes);
                            String json = JSONHandler.buildJSONPlace(r.getPlayerColor(), source, changes);
                            p.send(json);


                            if (board.isFinished()) {
                                //todo send end data
                            } else {
                                Bot bot = r.getBot();
                                Future<Action> reaction = bot.submit(board);
                                botAction(reaction.get(), board);
                            }
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
                        if (r.getBoard().submit(a)) {
                            String json = JSONHandler.buildJSONPass(r.getPlayerColor());
                            p.send(json);

                            if (board.isFinished()) {
                                //todo send end data
                            } else {
                                //TODO send action to bot
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

    private void botAction (Action action, Board board) {
        if (action instanceof PlacingAction) {
            PlacingAction a = (PlacingAction) action;

        } else if (action instanceof PassAction) {
            PassAction a = (PassAction) action;

        }
    }
}
