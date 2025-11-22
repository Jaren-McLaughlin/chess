package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.HttpException;
import websocket.messages.NotificationMessage;

import java.util.Arrays;

import static ui.EscapeSequences.NEW_LINE;

public class GamePlay implements CommandHandler, NotificationHandler {
    private final WebSocketFacade webSocket;

    public GamePlay(ClientSession clientSession) {
        WebSocketFacade ws = null;
        try {
            ws = new WebSocketFacade(clientSession.getApiUrl(), this, clientSession);
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
            case "help" -> help();
            case "leave" -> leave(clientSession);
            case "makemove" -> makeMove(clientSession, parameters);
            case "redrawboard" -> redrawBoard(clientSession);
            case "resign" -> resign(clientSession);
            case "showmoves" -> showMoves(clientSession, parameters);
            default -> unknownCommand(command);
        };
    }

    public void message(NotificationMessage serverMessage) {
        System.out.print(serverMessage.getMessage() + NEW_LINE);
        System.out.print("> ");
    }

    private String help() {
        String helpPrompt = """
            Game Play Commands:
            help - Shows available commands
            leave - Stop playing/observing a game
            makeMove <Col><Row> <Col><Row> - Move a piece
            redrawBoard - Redraws the board
            resign - Forfeits the game
            showMoves <Col><Row> - Highlights all legal moves for a piece
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

    private String makeMove (ClientSession clientSession, String[] parameters) {
        if (parameters.length < 2) {
            System.out.println("Invalid options for observing a game, please follow this format: makeMove <Col><Row> <Col><Row>");
            return null;
        }
        ChessPosition startPosition = makeChessPosition(parameters[0]);
        ChessPosition endPosition = makeChessPosition(parameters[1]);
        ChessPiece.PieceType pawnPromotion = null;
        if (parameters.length == 3) {
            try {
                pawnPromotion = ChessPiece.PieceType.valueOf(parameters[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid pawn promotion, possible promotions: ");
                return null;
            }
        }
        ChessMove move = new ChessMove(startPosition, endPosition, pawnPromotion);
        webSocket.makeMove(clientSession, move);
        return "Success";
    }

    private String redrawBoard(ClientSession clientSession) {
        webSocket.redrawBoard(clientSession);
        return "Success";
    }

    private String resign(ClientSession clientSession) {
        webSocket.resign(clientSession);
        return "Success";
    }

    private String showMoves(ClientSession clientSession, String[] parameters) {
        if (parameters.length < 1) {
            System.out.println("Invalid options for observing a game, please follow this format: showmoves <Col><Row>");
            return null;
        }
        ChessPosition chessPosition = makeChessPosition(parameters[0]);
        webSocket.showMoves(clientSession, chessPosition);
        return "Success";
    }

    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of game play commands");
        return null;
    }

    private ChessPosition makeChessPosition(String rowColCombo) {
        try {
            char charCol = rowColCombo.charAt(0);
            char charRow = rowColCombo.charAt(1);
            int intCol = charCol - 'a' + 1;
            int intRow = charRow - '0';
            return new ChessPosition(intRow, intCol);
        } catch (Exception error) {
            System.out.println("There was an error with the move, make sure each moveset is <Col><Row> for example d4");
        }
        return null;
    }
}
