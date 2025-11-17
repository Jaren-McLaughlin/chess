package server;

//import jakarta.websocket.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;

public class ConnectionManager {
    private final Map<Session, Session> sessions = new ConcurrentHashMap<>();

    public void add(Session session) {
        sessions.put(session, session);
    }

    public void remove(Session session) {
        sessions.remove(session);
    }

    public void messageOthers(ServerMessage serverMessage, Session exludedSession) throws IOException {
        String message = serverMessage.toString();
        for (Session session : sessions.values()) {
            if (session.isOpen()) {
                if (session == exludedSession) continue;
                session.getRemote().sendString(message);
            }
        }
    }
    public void messageUser(ServerMessage serverMessage, Session session) throws IOException {
        String message = serverMessage.toString();
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }
}
