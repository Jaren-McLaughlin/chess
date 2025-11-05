package ui;

import exception.HttpException;
import model.AuthData;
import model.GameData;
import model.GameListData;
import server.ServerFacade;

import java.util.Arrays;

public class PostLogin implements CommandHandler {
    public String executeCommand(Session session, ServerFacade serverFacade, String input) {
        String[] values = input.toLowerCase().split(" ");
        String command = (values.length > 0) ? values[0] : "help";
        String[] parameters = Arrays.copyOfRange(values, 1, values.length);
        return switch (command) {
            case "creategame" -> createGame(session, serverFacade, parameters);
            case "help" -> help();
            case "listgames" -> listGames(session, serverFacade);
            case "logout" -> logout(session, serverFacade);
//            case "observegame" -> observevGame(session, serverFacade, parameters);
//            case "playgame" -> playGame(session, serverFacade, parameters);
            default -> unknownCommand(command);
        };
    }
    private String createGame(Session session, ServerFacade serverFacade, String[] input) {
        GameData response;
        try {
            response = serverFacade.createGame(
                new GameData(0, null, null, input[0], null),
                session.getAuthToken()
            );
        } catch (HttpException error) {
            System.out.println(error.getStatus() + ": " + error.getMessage());
            return null;
        }
        System.out.println("Game successfully created with id: " + response.gameID());
        return "Success";
    }
    private String help() {
        String helpPrompt = """
            Logged In Commands:
            createGame - End your connection
            help - Shows available commands
            listGames - See all games
            logout - Logout of your account
            observeGame - Watch a game
            playGame - Join a game
        """;
        System.out.println(helpPrompt);
        return null;
    }
    private String listGames(Session session, ServerFacade serverFacade) {
        GameListData response;
        try {
            response = serverFacade.getGameList(session.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getStatus() + ": " + error.getMessage());
            return null;
        }
        System.out.println("List of games: " + response);
        return "Success";
    }
    private String logout(Session session, ServerFacade serverFacade) {
        try {
            serverFacade.logout(session.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getStatus() + ": " + error.getMessage());
            return null;
        }
        session.setCommandHandler(new PreLogin());
        session.setAuthToken(null);
        return "Success";
    }
//    private String observevGame() {
//
//    }
//    private String playGame() {
//
//    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of logged in commands");
        return null;
    }
}
