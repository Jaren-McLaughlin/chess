package client;

public class ClientSession {
    private String authToken = "708c5e83-330a-41ad-9706-fdbdb029969e";
    private CommandHandler commandHandler = new PreLogin();;
    private int gameId = 1;
    private String apiUrl;

    public String getAuthToken() {
        return authToken;
    }
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
    public int getGameId() { return gameId; }
    public String getApiUrl() {
        return apiUrl;
    }

    public void setAuthToken (String authToken) {
        this.authToken = authToken;
    }
    public void setCommandHandler(CommandHandler newCommandHandler) {
        this.commandHandler = newCommandHandler;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    public void setApiUrl (String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
