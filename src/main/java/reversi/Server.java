package reversi;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebListener;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;

/**
 * Created by Claudio on 17.05.2017.
 */
@ServerEndpoint("/reversiServer")
@WebListener
public class Server {
    private final Map<String, Session> users = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session client) {
        users.putIfAbsent(client.getId(), client);
    }

    @OnClose
    public void onClose(Session client) {
        users.remove(client.getId());
    }

    @OnError
    public void onError(Session client) {

    }

    @OnMessage
    public void onMessage(String message, Session client) {

    }
}
