package server;

//import jakarta.websocket.Session;
import com.google.gson.Gson;
import websocket.messages.NotificationMessage;
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

    public void messageOthers(NotificationMessage serverMessage, Session exludedSession) throws IOException {
        String message = new Gson().toJson(serverMessage);
        System.out.println(message);
        for (Session session : sessions.values()) {
            if (session.isOpen()) {
                if (session == exludedSession) continue;
                session.getRemote().sendString(message);
            }
        }
    }

    public void messageUser(NotificationMessage serverMessage, Session session) throws IOException {
//        String message = serverMessage.toString();
        String message = new Gson().toJson(serverMessage);
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }
}
