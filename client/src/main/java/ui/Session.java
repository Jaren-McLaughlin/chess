package ui;

public class Session {
    private CommandHandler commandHandler = new PreLogin();
    private String authToken = null;

    public String getAuthToken() {
        return authToken;
    }
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setAuthToken (String authToken) {
        this.authToken = authToken;
    }
    public void setCommandHandler(CommandHandler newCommandHandler) {
        this.commandHandler = newCommandHandler;
    }
}
