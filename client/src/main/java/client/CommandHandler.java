package client;

public interface CommandHandler {
    String executeCommand(Session session, ServerFacade serverFacade, String input);
}
