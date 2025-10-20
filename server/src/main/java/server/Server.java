package server;

import com.google.gson.Gson;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.MemoryDataAccess.AuthMemoryDao;
import dataaccess.MemoryDataAccess.GameMemoryDao;
import dataaccess.MemoryDataAccess.UserMemoryDao;
import dataaccess.UserDao;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;
import exception.HttpException;
import java.util.Map;

public class Server {
    private final Javalin javalin;
    private final GameService gameService;
    private final UserService userService;
    private final AuthDao authDao;

    public Server() {
        this.authDao = new AuthMemoryDao();
        GameDao gameDao = new GameMemoryDao();
        UserDao userDao = new UserMemoryDao();
        this.gameService = new GameService(gameDao);
        this.userService = new UserService(authDao, userDao);
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
        // User Handlers
            .delete("/session", this::logout)
            .post("/session", this::login)
            .post("/user", this::createUser)
        // Game Handlers
            .delete("/db", this::clearDb)
            .get("/game", this::getGameList)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .exception(HttpException.class, (error, context) -> {
                context.status(error.getStatus())
                .json(error.toJson());
            })
            .exception(Exception.class, (error, context) -> {
                context.status(500).json(Map.of(
                "error", error.getMessage()
                ));
            });
    }
    private void logout (Context context) throws HttpException {
        String authToken = context.header("Authorization");
        userService.verifyToken(authToken);
        userService.logout(authToken);
    }

    private void createUser (Context context) throws HttpException {
        UserData userData = new Gson().fromJson(context.body(), UserData.class);
        AuthData response = userService.createUser(userData);
        context.json(new Gson().toJson(response));
    }

    private void login (Context context) throws HttpException {
        UserData userData = new Gson().fromJson(context.body(), UserData.class);
        AuthData response = userService.login(userData);
        context.json(new Gson().toJson(response));
    }

    private void clearDb (Context context) throws HttpException {
        gameService.clearDb();
        userService.clearDb();
    }

    private void getGameList (Context context) throws HttpException {
        String authToken = context.header("Authorization");
        userService.verifyToken(authToken);
        GameListData response = gameService.getGameList();
        context.json(new Gson().toJson(response));
    }

    private void createGame (Context context) throws HttpException {
        String authToken = context.header("Authorization");
        userService.verifyToken(authToken);
        GameData gameData = new Gson().fromJson(context.body(), GameData.class);
        GameData response = gameService.createGame(gameData);
        context.json(new Gson().toJson(response));
    }

    private void joinGame (Context context) throws HttpException {
        String authToken = context.header("Authorization");
        userService.verifyToken(authToken);
        String username;
        try {
            username = authDao.getUserByToken(authToken);
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Failed to get user");
        }
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
