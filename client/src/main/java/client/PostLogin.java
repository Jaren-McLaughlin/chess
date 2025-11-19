package client;

import chess.ChessBoard;
import chess.ChessGame;
import exception.HttpException;
import model.GameData;
import model.GameListData;
import model.JoinGameData;
import ui.ChessBoardUi;
import java.util.Arrays;
import java.util.HashMap;

public class PostLogin implements CommandHandler {
    private HashMap<Integer, Integer> gameIdMap = new HashMap<>();
    public String executeCommand(ClientSession clientSession, ServerFacade serverFacade, String input) {
        String[] values = input.toLowerCase().split(" ");
        String command = (values.length > 0) ? values[0] : "help";
        String[] parameters = Arrays.copyOfRange(values, 1, values.length);
        return switch (command) {
            case "creategame" -> createGame(clientSession, serverFacade, parameters);
            case "help" -> help();
            case "listgames" -> listGames(clientSession, serverFacade);
            case "logout" -> logout(clientSession, serverFacade);
            case "observegame" -> observeGame(clientSession, serverFacade, parameters);
            case "playgame" -> playGame(clientSession, serverFacade, parameters);
            default -> unknownCommand(command);
        };
    }
    private String createGame(ClientSession clientSession, ServerFacade serverFacade, String[] input) {
        if (input.length < 1) {
            System.out.println("Invalid options for creating a game, please follow this format: createGame <Game Name>");
            return null;
        }
        GameData response;
        try {
            response = serverFacade.createGame(
                new GameData(0, null, null, input[0], null),
                clientSession.getAuthToken()
            );
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        System.out.println("Game successfully created with id: " + response.gameID());
        return "Success";
    }
    private String help() {
        String helpPrompt = """
            Logged In Commands:
            createGame <Game Name> - End your connection
            help - Shows available commands
            listGames - See all games
            logout - Logout of your account
            observeGame <gameId> - Watch a game
            playGame <Team Color [WHITE|BLACK]> <gameId> - Join a game
        """;
        System.out.println(helpPrompt);
        return null;
    }
    private String listGames(ClientSession clientSession, ServerFacade serverFacade) {
        GameListData response;
        try {
            response = serverFacade.getGameList(clientSession.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        }
        if (response.games().isEmpty()) {
            System.out.println("No games found");
            return "Success";
        }
        int i = 0;
        gameIdMap.clear();
        for (GameData gameData: response.games()) {
            gameIdMap.put(i, gameData.gameID());
            System.out.println(
                "Game " + i +
                " [Game Name: \"" + gameData.gameName() +
                "\", White Player: \"" + gameData.whiteUsername() +
                "\", Black Player: \"" + gameData.blackUsername() +
                "\"]"
            );
            i++;
        }
        return "Success";
    }
    private String logout(ClientSession clientSession, ServerFacade serverFacade) {
        try {
            serverFacade.logout(clientSession.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        }
        clientSession.setCommandHandler(new PreLogin());
        clientSession.setAuthToken(null);
        System.out.println("Successfully logged out, type \"help\" to see a list of logged out commands");
        return "Success";
    }
    private String observeGame(ClientSession clientSession, ServerFacade serverFacade, String[] input) {
        if (input.length < 1) {
            System.out.println("Invalid options for observing a game, please follow this format: observeGame <gameId>");
            return null;
        }
        Integer gameId;
        GameData gameData;
        try {
            int displayId = Integer.parseInt(input[0]);
            gameId = gameIdMap.get(displayId);
            if (gameId == null) {
//                System.out.println("No game to observe");
//                return null;
                // So I don't have to keep doing listGames
                gameId = displayId;
            }
            gameData = serverFacade.getGameDetails(gameId, clientSession.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        if (gameData == null) {
            System.out.println("No game to observe");
            return null;
        }
        GamePlay command = new GamePlay(clientSession);
        clientSession.setCommandHandler(command);
        clientSession.setGameId(gameId);
        command.connectToGame(clientSession);
        System.out.println("Welcome to the gameplay, type \"help\" to see list commands");
        return "Success";
    }
    private String playGame(ClientSession clientSession, ServerFacade serverFacade, String[] input) {
        if (input.length < 2) {
            System.out.println("Invalid options for playing a game, please follow this format:  playGame <Team Color [WHITE|BLACK]> <gameId>");
            return null;
        }
        Integer gameId;
        ChessGame.TeamColor teamColor;
        String authToken = clientSession.getAuthToken();
        try {
            teamColor = ChessGame.TeamColor.valueOf(input[0].toUpperCase());
            int displayId = Integer.parseInt(input[1]);
            gameId = gameIdMap.get(displayId);
            if (gameId == null) {
//                System.out.println("No game to observe");
//                return null;
                // So I don't have to keep doing listGames
                gameId = displayId;
            }
            serverFacade.joinGame(new JoinGameData(teamColor, gameId), authToken);
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        GamePlay command = new GamePlay(clientSession);
        clientSession.setCommandHandler(command);
        clientSession.setGameId(gameId);
        command.connectToGame(clientSession);
        System.out.println("Welcome to the gameplay, type \"help\" to see list commands");
        return "Success";
    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of logged in commands");
        return null;
    }
}
