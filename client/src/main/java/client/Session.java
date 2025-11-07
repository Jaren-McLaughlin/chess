package ui;

public class Session {
    private String authToken = null;
    private CommandHandler commandHandler = new PreLogin();
    private int gameId = 0;

    public String getAuthToken() {
        return authToken;
    }
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
    public int getGameId() { return gameId; }

    public void setAuthToken (String authToken) {
        this.authToken = authToken;
    }
    public void setCommandHandler(CommandHandler newCommandHandler) {
        this.commandHandler = newCommandHandler;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
