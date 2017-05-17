package reversi;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;

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
@ServerEndpoint("/annotated")
public class MyServletContextListenerAnnotated implements ServletContextListener {
    private final Map<String, Session> users = new ConcurrentHashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final ServerContainer serverContainer = (ServerContainer) servletContextEvent.getServletContext()
                .getAttribute("javax.websocket.server.ServerContainer");

        try {
            serverContainer.addEndpoint(MyServletContextListenerAnnotated.class);
        } catch (DeploymentException e) {
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

    @OnError
    public void onError(Session client) {

    }

    @OnMessage
    public void onMessage(String message, Session client) {

    }
}
