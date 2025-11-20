package client;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exception.HttpException;
import jakarta.websocket.*;
import model.GameData;
import ui.ChessBoardUi;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommandMessage;
import websocket.messages.GameBoardMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.NEW_LINE;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler ;
    ClientSession clientSession;

    public WebSocketFacade(String url, NotificationHandler notificationHandler, ClientSession clientSession) throws HttpException{
        try {
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            this.clientSession = clientSession;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.print(NEW_LINE);
                    NotificationMessage serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                    ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();
                    if (messageType == ServerMessage.ServerMessageType.LOAD_GAME) {
                        GameBoardMessage gameBoardMessage = new Gson().fromJson(serverMessage.getMessage(), GameBoardMessage.class);
                        if (gameBoardMessage.getDisplayFrom() == ChessGame.TeamColor.BLACK) {
                            ChessBoardUi.drawFromBlack(gameBoardMessage);
                        } else {
                            ChessBoardUi.drawFromWhite(gameBoardMessage);
                        }
                        System.out.print("> ");
                    } else {
                        notificationHandler.message(serverMessage);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw HttpException.internalServerError("Something went wrong");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(ClientSession clientSession) {
        UserGameCommandMessage userGameCommand = new UserGameCommandMessage(UserGameCommand.CommandType.CONNECT, clientSession.getAuthToken(), clientSession.getGameId(), null);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }

    public void leaveGame(ClientSession clientSession) {
        UserGameCommandMessage userGameCommand = new UserGameCommandMessage(UserGameCommand.CommandType.LEAVE, clientSession.getAuthToken(), clientSession.getGameId(), null);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }

    public void makeMove(ClientSession clientSession, String[] parameters) {
        UserGameCommandMessage userGameCommand = new UserGameCommandMessage(UserGameCommand.CommandType.MAKE_MOVE, clientSession.getAuthToken(), clientSession.getGameId(), new Gson().toJson(parameters));
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }

    public void redrawBoard(ClientSession clientSession) {
        UserGameCommandMessage userGameCommand = new UserGameCommandMessage(UserGameCommand.CommandType.REDRAW_BOARD, clientSession.getAuthToken(), clientSession.getGameId(), null);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }

    public void resign(ClientSession clientSession) {
        UserGameCommandMessage userGameCommand = new UserGameCommandMessage(UserGameCommand.CommandType.RESIGN, clientSession.getAuthToken(), clientSession.getGameId(), null);
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }

    public void showMoves(ClientSession clientSession, ChessPosition chessPosition) {
        UserGameCommandMessage userGameCommand = new UserGameCommandMessage(UserGameCommand.CommandType.SHOW_MOVES, clientSession.getAuthToken(), clientSession.getGameId(), new Gson().toJson(chessPosition));
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }
}
