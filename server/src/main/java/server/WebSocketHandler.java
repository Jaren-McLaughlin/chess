package server;
import chess.ChessGame;
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
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> createConnection(userGameCommand, ctx.session);
            case LEAVE -> leaveGame(userGameCommand, ctx.session);
            case MAKE_MOVE -> movePiece(userGameCommand, ctx.session);
            case RESIGN -> resign(userGameCommand, ctx.session);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
    }


    private void canMove() {

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

    private void createConnection (UserGameCommand userGameCommand, Session session) {
        String user = getUserByAuth(userGameCommand.getAuthToken());
        if (user == null) {
            System.out.println("Error: Somehow you lost your authToken");
            return;
        }
        connections.add(session);
        ChessGame.TeamColor teamColor = getTeamColor(userGameCommand.getGameID(), user);
        String messageString;
        if (teamColor != null) {
            messageString = user + " has joined the game playing " + teamColor;
        } else {
            messageString = user + " has started observing the game";
        }
        NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, messageString);
        try {
            connections.messageOthers(message, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void leaveGame (UserGameCommand userGameCommand, Session session) {
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

    private void movePiece (UserGameCommand userGameCommand, Session session) {

    }

    private void resign (UserGameCommand userGameCommand, Session session) {

    }
}
