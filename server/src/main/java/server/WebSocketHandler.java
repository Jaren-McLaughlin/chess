package server;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.mysqlataaccess.AuthSQLDao;
import dataaccess.mysqlataaccess.GameSQLDao;
import exception.HttpException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommandMessage;
import websocket.messages.GameBoardMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
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

    private void canMove() {

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

    private ChessGame.TeamColor getTeamColor (int gamdId, String username) {
        try {
            GameData gameDetails = gameDao.getGame(gamdId);
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

    }

    private void resign (UserGameCommandMessage userGameCommand, Session session) {

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
