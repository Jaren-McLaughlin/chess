package server;
import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.mysqlataaccess.AuthSQLDao;
import exception.HttpException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

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
            AuthDao authDao = new AuthSQLDao();
            return authDao.getUserByToken(authToken);
        } catch (DataAccessException error) {
            System.out.println("couldn't get data");
            return null;
        }
    }

    private void createConnection (UserGameCommand userGameCommand, Session session) {
        String user = getUserByAuth(userGameCommand.getAuthToken());
        if (user == null) {
            System.out.println("Error: Somehow you lost your authToken");
            return;
        }
        connections.add(session);
        String messageString = user + " joined the game";
        NotificationMessage message = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, messageString);
        try {
            connections.messageOthers(message, session);
        } catch (IOException error) {
            System.out.println("There was an error with this");
            error.printStackTrace();
        }
    }

    private void leaveGame (UserGameCommand userGameCommand, Session session) {
        connections.remove(session);
    }

    private void movePiece (UserGameCommand userGameCommand, Session session) {

    }

    private void resign (UserGameCommand userGameCommand, Session session) {

    }
}
