package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exception.HttpException;
import model.AuthData;
import model.GameData;
import model.GameListData;
import model.JoinGameData;
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
            case "observegame" -> observeGame(session, serverFacade, parameters);
            case "playgame" -> playGame(session, serverFacade, parameters);
            default -> unknownCommand(command);
        };
    }
    private String createGame(Session session, ServerFacade serverFacade, String[] input) {
        if (input.length < 1) {
            System.out.println("Invalid options for creating a game, please follow this format: createGame <Game Name>");
            return null;
        }
        GameData response;
        try {
            response = serverFacade.createGame(
                new GameData(0, null, null, input[0], null),
                session.getAuthToken()
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
    private String listGames(Session session, ServerFacade serverFacade) {
        GameListData response;
        try {
            response = serverFacade.getGameList(session.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        }
        for (GameData gameData: response.games()) {
            System.out.println(
                "game " + gameData.gameID() +
                " [Game Name: \"" + gameData.gameName() +
                "\", White Player: \"" + gameData.whiteUsername() +
                "\", Black Player: \"" + gameData.blackUsername() +
                "\"]"
            );
        }
        return "Success";
    }
    private String logout(Session session, ServerFacade serverFacade) {
        try {
            serverFacade.logout(session.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        }
        session.setCommandHandler(new PreLogin());
        session.setAuthToken(null);
        System.out.println("Successfully logged out, type \"help\" to see a list of logged out commands");
        return "Success";
    }
    private String observeGame(Session session, ServerFacade serverFacade, String[] input) {
        if (input.length < 1) {
            System.out.println("Invalid options for observing a game, please follow this format: observeGame <gameId>");
            return null;
        }
        int gameId;
        GameData gameData;
        try {
            gameId = Integer.parseInt(input[0]);
            gameData = serverFacade.getGameDetails(gameId, session.getAuthToken());
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        session.setCommandHandler(new GamePlay());
        session.setGameId(gameId);
        ChessBoard chessBoard = gameData.game().getBoard();
        ChessBoardUi.drawFromWhite(chessBoard);
        System.out.println("Welcome to the gameplay, type \"help\" to see list commands");
        return "Success";
    }
    private String playGame(Session session, ServerFacade serverFacade, String[] input) {
        if (input.length < 2) {
            System.out.println("Invalid options for playing a game, please follow this format:  playGame <Team Color [WHITE|BLACK]> <gameId>");
            return null;
        }
        int gameId;
        ChessGame.TeamColor teamColor;
        String authToken = session.getAuthToken();
        try {
            teamColor = ChessGame.TeamColor.valueOf(input[0].toUpperCase());
            gameId = Integer.parseInt(input[1]);
            serverFacade.joinGame(new JoinGameData(teamColor, gameId), authToken);
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        session.setCommandHandler(new GamePlay());
        session.setGameId(gameId);
        GameData gameData;
        try {
            gameData = serverFacade.getGameDetails(gameId, authToken);
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        }
        System.out.println(gameData.toString());
        ChessBoard chessBoard = gameData.game().getBoard();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            ChessBoardUi.drawFromWhite(chessBoard);
        } else {
            ChessBoardUi.drawFromBlack(chessBoard);
        }
        System.out.println("Welcome to the gameplay, type \"help\" to see list commands");
        return "Success";
    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of logged in commands");
        return null;
    }
}
