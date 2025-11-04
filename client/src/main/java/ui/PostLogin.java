package ui;

import server.ServerFacade;

import java.util.Arrays;

public class PostLogin implements CommandHandler {
    public String executeCommand(Session session, ServerFacade serverFacade, String input) {
        String[] values = input.toLowerCase().split(" ");
        String command = (values.length > 0) ? values[0] : "help";
        String[] parameters = Arrays.copyOfRange(values, 1, values.length);
        return switch (command) {
//            case "createGame" -> quit();
            case "help" -> help();
//            case "listGames" -> register(session, serverFacade, parameters);
//            case "logout" -> login(session, serverFacade, parameters);
//            case "observeGame" -> quit();
//            case "playGame" -> quit();
            default -> unknownCommand(command);
        };
    }
    private String help() {
        String helpPrompt = """
            Commands:
            help - Shows available commands
            logout - Logout of your account
            createGame - End your connection
            listGames - See all games
            playGame - Join a game
            observeGame - Watch a game
        """;
        System.out.println(helpPrompt);
        return null;
    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of commands");
        return null;
    }
}
