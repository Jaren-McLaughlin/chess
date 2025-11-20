package websocket.commands;

public class UserGameCommandMessage extends  UserGameCommand{
    private final String message;
    public UserGameCommandMessage(CommandType commandType, String authToken, Integer gameID, String message) {
        super(commandType, authToken, gameID);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
