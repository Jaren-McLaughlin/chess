package client;

import exception.HttpException;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.NEW_LINE;

public class GamePlay implements CommandHandler, NotificationHandler {
    private final WebSocketFacade webSocket;

    public GamePlay(ClientSession clientSession) {
        WebSocketFacade ws = null;
        try {
            ws = new WebSocketFacade(clientSession.getApiUrl(), this);
        } catch (HttpException error) {
            System.out.println("Couldn't fetch websocket");
        }
        this.webSocket = ws;
    }

    public void connectToGame(ClientSession clientSession) {
        webSocket.connectToGame(clientSession);
    }

    public String executeCommand(ClientSession clientSession, ServerFacade serverFacade, String input) {
        String[] values = input.toLowerCase().split(" ");
        String command = (values.length > 0) ? values[0] : "help";
        String[] parameters = Arrays.copyOfRange(values, 1, values.length);
        return switch (command) {
            case "redrawboard" -> drawBoard(clientSession, serverFacade);
            case "help" -> help();
            case "leave" -> leave(clientSession);
            default -> unknownCommand(command);
        };
    }

    public void message(NotificationMessage serverMessage) {
        System.out.print(serverMessage.getMessage() + NEW_LINE);
        System.out.print("> ");
    }

    private String drawBoard(ClientSession clientSession, ServerFacade serverFacade) {
        try {
            serverFacade.getGameList(clientSession.getAuthToken());
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
            makeMove <Row><Col> <Row><Col> - Move a piece
            redrawBoard - Redraws the board
            resign - Forfeits the game
            showMoves - Highlights all legal moves for a piece
        """;
        System.out.println(helpPrompt);
        return "success";
    }
    private String leave(ClientSession clientSession) {
        webSocket.leaveGame(clientSession);
        clientSession.setGameId(0);
        clientSession.setCommandHandler(new PostLogin());
        System.out.println("Successfully left game");
        return "success";
    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of game play commands");
        return null;
    }
}
