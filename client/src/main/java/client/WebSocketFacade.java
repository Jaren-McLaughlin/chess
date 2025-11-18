package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import exception.HttpException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler ;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws HttpException{
        try {
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    NotificationMessage serverMessage = new Gson().fromJson(message, NotificationMessage.class);
                    notificationHandler.message(serverMessage);
                }
            });
        }catch (DeploymentException | IOException | URISyntaxException ex) {
            throw HttpException.internalServerError("Something went wrong");
        } catch (JsonSyntaxException error) {
            error.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(ClientSession clientSession) {
        UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, clientSession.getAuthToken(), clientSession.getGameId());
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException error) {
            System.out.println("There was an error");
        }
    }
}
