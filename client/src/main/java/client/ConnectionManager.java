package client;

import jakarta.websocket.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;

public class ConnectionManager {
    private final HashMap<Session, Session> sessions = new HashMap<>();

    public void add(Session session) {
        sessions.put(session, session);
    }

    public void remove(Session session) {
        sessions.remove(session);
    }

    public void broadcast(ServerMessage serverMessage, Session exludedSession) throws IOException {
        String message = serverMessage.toString();
        for (Session session : sessions.values()) {
            if (session.isOpen()) {
                if (session == exludedSession) continue;
                session.getBasicRemote().sendText(message);
            }
        }
    }
}
