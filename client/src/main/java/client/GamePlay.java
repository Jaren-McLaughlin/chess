package client;

import exception.HttpException;

import java.util.Arrays;

public class GamePlay implements CommandHandler {
    public String executeCommand(Session session, ServerFacade serverFacade, String input) {
        String[] values = input.toLowerCase().split(" ");
        String command = (values.length > 0) ? values[0] : "help";
        String[] parameters = Arrays.copyOfRange(values, 1, values.length);
        return switch (command) {
            case "redrawboard" -> drawBoard(session, serverFacade);
            case "help" -> help();
            case "leave" -> leave(session, serverFacade);
            default -> unknownCommand(command);
        };
    }
    private String drawBoard(Session session, ServerFacade serverFacade) {
        try {
            serverFacade.getGameList(session.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getStatus() + error.getMessage());
        }
        return "Success";
    }
    private String help() {
        String helpPrompt = """
            Game Play Commands:
            help - Shows available commands
            leave - Stop playing/observing a game
        """;
        System.out.println(helpPrompt);
        return "success";
    }
    private String leave(Session session, ServerFacade serverFacade) {
        session.setGameId(0);
        session.setCommandHandler(new PostLogin());
        System.out.println("Successfully left game");
        return "success";
    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of game play commands");
        return null;
    }
}
