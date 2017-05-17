package reversi;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;

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
@ServerEndpoint("/server")
public class ReversiServer implements ServletContextListener {
    private final Map<String, Session> users = new ConcurrentHashMap<>();

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
        users.putIfAbsent(client.getId(), client);
    }

    @OnClose
    public void onClose(Session client) {
        users.remove(client.getId());
    }

    @OnMessage
    public void onMessage(String message, Session client) {

    }
}
