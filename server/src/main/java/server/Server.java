package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import model.*;
import service.*;

public class Server {
    private final Javalin javalin;
    private final AuthService authService = new AuthService();
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        // Auth Handlers
                .delete("/session", this::logout)
                .post("/session", this::createUser)
                .post("/user", this::login)
        // Game Handlers
                .delete("/db", this::clearDb)
                .get("/game", this::getGameList)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
//                .exception()
                ;
    }

    private void logout (Context context) {
        // authorization: <authToken>
        authService.verifyToken();
        userService.logout();
    }

    private void createUser (Context context) {
        // body: { "username":"", "password":"", "email":"" }
        UserData userData = new Gson().fromJson(context.body(), UserData.class);
        // response: { "username":"", "authToken":"" }
        AuthData response = userService.createUser(userData);
        context.json(new Gson().toJson(response));
    }

    private void login (Context context) {
        // body: { "username":"", "password":"" }
        UserData userData = new Gson().fromJson(context.body(), UserData.class);
        // response: { "username":"", "authToken":"" }
        AuthData response = userService.login(userData);
        context.json(new Gson().toJson(response));
    }

    private void clearDb (Context context) {
        gameService.clearDb();
    }

    private void getGameList (Context context) {
        // 	authorization: <authToken>
        authService.verifyToken();
        // response: { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
        GameData response = gameService.getGameList();
        context.json(new Gson().toJson(response));
    }

    private void createGame (Context context) {
        // 	authorization: <authToken>
        authService.verifyToken();
        // body: { "gameName":"" }
        GameData gameData = new Gson().fromJson(context.body(), GameData.class);
        GameData response = gameService.createGame(gameData);
        // response: { "gameID": 1234 }
        context.json(new Gson().toJson(response));
    }

    private void joinGame (Context context) {
        // 	authorization: <authToken>
        authService.verifyToken();
        // body: { "playerColor":"WHITE/BLACK", "gameID": 1234 }
        JoinGameData joinGameData = new Gson().fromJson(context.body(), JoinGameData.class);
        gameService.joinGame(joinGameData);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
