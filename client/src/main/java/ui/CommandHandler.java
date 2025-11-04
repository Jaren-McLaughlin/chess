package ui;

import server.ServerFacade;

public interface CommandHandler {
    String executeCommand(Session session, ServerFacade serverFacade, String input);
}
