package server;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;
import exception.HttpException;
import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();
    private final AuthDao authDao = new AuthDao();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        // User Handlers
            .delete("/session", this::logout)
            .post("/session", this::createUser)
            .post("/user", this::login)
        // Game Handlers
            .delete("/db", this::clearDb)
            .get("/game", this::getGameList)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .exception(HttpException.class, (error, context) -> {
                context.status(error.getStatus()).json(Map.of("error", error.getMessage()));
            })
            .exception(Exception.class, (error, context) -> {
                context.status(500).json(Map.of(
                "error", error.getMessage()
                ));
            });
    }

    private void logout (Context context) throws HttpException {
        // authorization: <authToken>
        String authToken = context.header("authToken");
        userService.verifyToken(authToken);
        userService.logout(authToken);
    }

    private void createUser (Context context) throws HttpException {
        // body: { "username":"", "password":"", "email":"" }
        UserData userData = new Gson().fromJson(context.body(), UserData.class);
        // response: { "username":"", "authToken":"" }
        AuthData response = userService.createUser(userData);
        context.json(new Gson().toJson(response));
    }

    private void login (Context context) throws HttpException {
        // body: { "username":"", "password":"" }
        UserData userData = new Gson().fromJson(context.body(), UserData.class);
        // response: { "username":"", "authToken":"" }
        AuthData response = userService.login(userData);
        context.json(new Gson().toJson(response));
    }

    private void clearDb (Context context) throws HttpException {
        gameService.clearDb();
        userService.clearDb();
    }

    private void getGameList (Context context) throws HttpException {
        String authToken = context.header("authToken");
        userService.verifyToken(authToken);
        GameListData response = gameService.getGameList();
        context.json(new Gson().toJson(response));
    }

    private void createGame (Context context) throws HttpException {
        String authToken = context.header("authToken");
        userService.verifyToken(authToken);
//        String username = authDao.getUserByToken(authToken);
        GameData gameData = new Gson().fromJson(context.body(), GameData.class);
        GameData response = gameService.createGame(gameData);
        // response: { "gameID": 1234 }
        context.json(new Gson().toJson(response));
    }

    private void joinGame (Context context) throws HttpException {
        // 	authorization: <authToken>
        String authToken = context.header("authToken");
        userService.verifyToken(authToken);
        String username = authDao.getUserByToken(authToken);
        JoinGameData joinGameData = new Gson().fromJson(context.body(), JoinGameData.class);
        gameService.joinGame(joinGameData, username);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
