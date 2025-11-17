package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    private final String message;
    public LoadGameMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
