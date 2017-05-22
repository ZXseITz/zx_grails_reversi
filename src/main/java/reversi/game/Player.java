package reversi.game;

import javax.websocket.Session;

/**
 * Created by Claudio on 21.05.2017.
 */
public class Player {
    private Session session;
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

    public boolean isInRound() {
        return round != null;
    }

    public void setRound(Round round) {
        this.round = round;
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
