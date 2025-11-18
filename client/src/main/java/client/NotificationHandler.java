package client;

import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void message(NotificationMessage message);
}
