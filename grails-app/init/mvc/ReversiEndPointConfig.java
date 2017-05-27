package mvc;

import reversi.ReversiServer;
import reversi.pvp.PVP;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by Claudio on 25.05.2017.
 */
public class ReversiEndPointConfig extends ServerEndpointConfig.Configurator {
    public static final ReversiServer reversiEndPoint = new ReversiServer(new PVP());

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        if (endpointClass.equals(ReversiServer.class)) {
            return (T) reversiEndPoint;
        }
        throw new InstantiationException();
    }
}
