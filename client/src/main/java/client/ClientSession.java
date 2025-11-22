package client;

import chess.ChessGame;

public class ClientSession {
    private String authToken = "708c5e83-330a-41ad-9706-fdbdb029969e";
    private CommandHandler commandHandler = new PreLogin();;
    private int gameId = 1;
    private String apiUrl;
    private ChessGame.TeamColor displayColor;

    public String getApiUrl() {
        return apiUrl;
    }
    public String getAuthToken() {
        return authToken;
    }
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
    public ChessGame.TeamColor getDisplayColor() {
        return displayColor;
    }
    public int getGameId() { return gameId; }

    public void setApiUrl (String apiUrl) {
        this.apiUrl = apiUrl;
    }
    public void setAuthToken (String authToken) {
        this.authToken = authToken;
    }
    public void setCommandHandler(CommandHandler newCommandHandler) {
        this.commandHandler = newCommandHandler;
    }
    public void setDisplayColor(ChessGame.TeamColor displayColor) {
        this.displayColor = displayColor;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
