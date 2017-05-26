package reversi.game;

import javax.websocket.Session;

/**
 * Created by Claudio on 21.05.2017.
 */
public class Player {
    public enum State {
        OFFLINE,
        ONLINE,
        WAITING,
        INGAME
    }

    private final Session session;
    private volatile State state;
    private Round round;

    public String getID() {
        return session.getId();
    }

    public Player(Session session) {
        this.session = session;
    }

    public Round getRound() {
        return round;
    }

//    public boolean isInRound() {
//        return round != null;
//    }

    public void setRound(Round round) {
        this.round = round;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void send(String json) {
        try {
            session.getBasicRemote().sendText(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
