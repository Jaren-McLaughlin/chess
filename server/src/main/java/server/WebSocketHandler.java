package server;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.mysqlataaccess.AuthSQLDao;
import dataaccess.mysqlataaccess.GameSQLDao;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommandMessage;
import websocket.messages.GameBoardMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDao authDao;
    private final GameDao gameDao;

    public WebSocketHandler() {
        try {
            this.authDao = new AuthSQLDao();
            this.gameDao = new GameSQLDao();
        } catch (DataAccessException error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        UserGameCommandMessage userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommandMessage.class);

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> createConnection(userGameCommand, ctx.session);
            case LEAVE -> leaveGame(userGameCommand, ctx.session);
            case MAKE_MOVE -> movePiece(userGameCommand, ctx.session);
            case RESIGN -> resign(userGameCommand, ctx.session);
            case REDRAW_BOARD -> redrawBoard(userGameCommand, ctx.session);
            case SHOW_MOVES -> showMoves(userGameCommand, ctx.session);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {}

    private boolean getCanMove(int gameId, String user) {
        boolean isTurn = false;
        try {
            GameData gameData = gameDao.getGame(gameId);
            ChessGame chessGame = gameData.game();
            ChessGame.TeamColor teamColor = getTeamColor(gameId, user);
            ChessGame.TeamColor playerTurn = chessGame.getTeamTurn();
            if (teamColor == playerTurn) {
                isTurn = true;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return isTurn;
    }

    private NotificationMessage sendChessBoard(int gameId, ChessGame.TeamColor teamColor, ChessPosition chessPosition) {
        try {
            GameData gameData = gameDao.getGame(gameId);
            GameBoardMessage gameBoardMessage = new GameBoardMessage(
                gameData.game(),
                teamColor
            );
            if (chessPosition != null) {
                gameBoardMessage.createPossibleMoves(chessPosition);
            }
            return new NotificationMessage(ServerMessage.ServerMessageType.LOAD_GAME, new Gson().toJson(gameBoardMessage));
        } catch (DataAccessException e) {
            System.out.println("There was an error");
        }
        return null;
    }

    private String getUserByAuth (String  authToken) {
        try {
            return authDao.getUserByToken(authToken);
        } catch (DataAccessException error) {
            System.out.println("couldn't get data");
            return null;
        }
    }

    private ChessGame.TeamColor getTeamColor (int gameId, String username) {
        try {
            GameData gameDetails = gameDao.getGame(gameId);
            if (Objects.equals(gameDetails.blackUsername(), username)) {
                return ChessGame.TeamColor.BLACK;
            } else if (Objects.equals(gameDetails.whiteUsername(), username)) {
                return ChessGame.TeamColor.WHITE;
            }
        } catch (DataAccessException error) {
            return null;
        }
        return null;
    }

    private boolean gameOver (int gameId, Session session) {
        ChessGame.GameStatus gameStatus = null;
        try {
            gameStatus = gameDao.getGameStatus(gameId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        if (gameStatus == ChessGame.GameStatus.PLAYING) {
            return false;
        }
        String message = "The game is over, you can no longer make a move";
        NotificationMessage gameOverMessage = new NotificationMessage(ServerMessage.ServerMessageType.ERROR, message);
        try {
            connections.messageUser(gameOverMessage, session);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private NotificationMessage validateGameState (int gameId, ChessGame.TeamColor opponentColor) {
        GameData gameData;
        try {
            gameData = gameDao.getGame(gameId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        ChessGame chessGame = gameData.game();
        if (chessGame.isInCheckmate(opponentColor)) {
            String message = opponentColor.toString() + " is in checkmate, game over";
            // Mark game as over
            try {
                gameDao.updateGameStatus(gameId, ChessGame.GameStatus.CHECKMATE);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
            return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        } else if (chessGame.isInStalemate(opponentColor)) {
            try {
                gameDao.updateGameStatus(gameId, ChessGame.GameStatus.STALEMATE);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
            String message = "The match is in a stalemate, game over";
            return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        } else if (chessGame.isInCheck(opponentColor)) {
            String message = opponentColor.toString() + " is in check";
            return new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        }
        return null;
    }

    private void createConnection (UserGameCommandMessage userGameCommand, Session session) {
        String user = getUserByAuth(userGameCommand.getAuthToken());
        int gameId = userGameCommand.getGameID();
        if (user == null) {
            System.out.println("Error: Somehow you lost your authToken");
            return;
        }
        connections.add(session);
        ChessGame.TeamColor teamColor = getTeamColor(gameId, user);
        String messageString;
        if (teamColor != null) {
            messageString = user + " has joined the game playing " + teamColor;
        } else {
            messageString = user + " has started observing the game";
        }
        NotificationMessage gameBoard = sendChessBoard(gameId, teamColor,null);
        NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, messageString);
        try {
            connections.messageUser(gameBoard, session);
            connections.messageOthers(message, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void leaveGame (UserGameCommandMessage userGameCommand, Session session) {
        String user = getUserByAuth(userGameCommand.getAuthToken());
        if (user == null) {
            System.out.println("Error: Somehow you lost your authToken");
            return;
        }
        connections.remove(session);
        ChessGame.TeamColor teamColor = getTeamColor(userGameCommand.getGameID(), user);
        String messageString;
        if (teamColor != null) {
            messageString = user + " has stopped playing the game as " + teamColor;
            try {
                gameDao.removeUser(userGameCommand.getGameID(), teamColor);
            } catch (DataAccessException error) {
                System.out.println("Error: We had a problem " + error);
                return;
            }
        } else {
            messageString = user + " has stopped observing the game";
        }
        NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, messageString);
        try {
            connections.messageOthers(message, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void movePiece (UserGameCommandMessage userGameCommand, Session session) {
        ChessMove chessMove = new Gson().fromJson(userGameCommand.getMessage(), ChessMove.class);
        int gameId = userGameCommand.getGameID();

        String user = getUserByAuth(userGameCommand.getAuthToken());
        if (user == null) {
            System.out.println("Error: Somehow you lost your authToken");
            return;
        }

        if (gameOver(gameId, session)) return;

        boolean canMove = getCanMove(gameId, user);
        if (!canMove) {
            String message = "It is not your turn to move";
            new NotificationMessage(ServerMessage.ServerMessageType.ERROR, message);
            return;
        }

        GameData gameData;
        try {
            gameData = gameDao.getGame(gameId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        ChessGame chessGame = gameData.game();
        try {
            chessGame.makeMove(chessMove);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }

        try {
            gameDao.updateGameBoard(chessGame, gameId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        ChessGame.TeamColor drawFrom = getTeamColor(gameId, user);
        NotificationMessage gameBoardMessage = sendChessBoard(gameId, drawFrom, null);

        ChessGame.TeamColor opponentColor;
        if (ChessGame.TeamColor.BLACK == drawFrom) {
            opponentColor = ChessGame.TeamColor.WHITE;
        } else {
            opponentColor = ChessGame.TeamColor.BLACK;
        }

        NotificationMessage gameStatusMessage = validateGameState(gameId, opponentColor);
        String message = "Movement happened";
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            connections.messageOthers(gameBoardMessage, session);
            connections.messageUser(gameBoardMessage, session);
            connections.messageOthers(notificationMessage, session);
            if (gameStatusMessage != null) {
                connections.messageOthers(gameStatusMessage, session);
                connections.messageUser(gameStatusMessage, session);
            }
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void resign (UserGameCommandMessage userGameCommand, Session session) {
        if (gameOver(userGameCommand.getGameID(), session)) return;
        try {
            gameDao.updateGameStatus(userGameCommand.getGameID(), ChessGame.GameStatus.RESIGNED);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        String message = "Resigned the game";
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            connections.messageOthers(notificationMessage, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void redrawBoard (UserGameCommandMessage userGameCommand, Session session) {
        String user = getUserByAuth(userGameCommand.getAuthToken());
        int gameId = userGameCommand.getGameID();
        ChessGame.TeamColor drawFrom = getTeamColor(gameId, user);
        NotificationMessage gameBoardMessage = sendChessBoard(gameId, drawFrom, null);
        try {
            connections.messageUser(gameBoardMessage, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void showMoves (UserGameCommandMessage userGameCommand, Session session) {
        ChessPosition chessPosition = new Gson().fromJson(userGameCommand.getMessage(), ChessPosition.class);
        int gameId = userGameCommand.getGameID();
        String user = getUserByAuth(userGameCommand.getAuthToken());
        ChessGame.TeamColor teamColor = getTeamColor(gameId, user);
        NotificationMessage gameBoardMessage = sendChessBoard(gameId, teamColor, chessPosition);
        try {
            connections.messageUser(gameBoardMessage, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }
}
