package client;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void message(ServerMessage message);
}
