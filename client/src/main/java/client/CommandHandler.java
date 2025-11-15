package client;

public interface CommandHandler {
    String executeCommand(ClientSession clientSession, ServerFacade serverFacade, String input);
}
