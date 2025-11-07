package client;

import chess.ChessGame;
import exception.HttpException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws HttpException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String apiUrl = "http://localhost:" + port;
        serverFacade = new ServerFacade(apiUrl);
        serverFacade.clearDb();
    }

    @AfterEach
    void clear() throws HttpException {
        serverFacade.clearDb();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void createGame() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        GameData response = serverFacade.createGame(new GameData(0, null, null, "MyGame", null), user.authToken());
        GameData expected = new GameData(1, null, null, "MyGame", null);
        Assertions.assertEquals(expected, response);
    }
    @Test
    public void notAuthorized() throws HttpException {
        GameData gameData = new GameData(0, null, null, "MyGame", null);
        HttpException thrownError = Assertions.assertThrows(
                HttpException.class, () ->   serverFacade.createGame(gameData, "Something")
        );
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    public void getGameDetails() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        serverFacade.createGame(new GameData(0, null, null, "MyGame", null), user.authToken());
        GameData expected = new GameData(1, null, null, "MyGame", new ChessGame());
        GameData response = serverFacade.getGameDetails(1, user.authToken());
        Assertions.assertEquals(expected, response);
    }
    @Test
    public void notAuthToGetGame() throws HttpException {
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   serverFacade.getGameDetails(1, "Something"));
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    public void getGameList() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        serverFacade.createGame(new GameData(0, null, null, "MyGame1", null), user.authToken());
        serverFacade.createGame(new GameData(0, null, null, "MyGame2", null), user.authToken());
        serverFacade.createGame(new GameData(0, null, null, "MyGame3", null), user.authToken());

        GameListData response = serverFacade.getGameList(user.authToken());
        Assertions.assertEquals(3, response.games().size());
    }
    @Test
    public void noGamesInDB() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        GameListData response = serverFacade.getGameList(user.authToken());
        Assertions.assertEquals(0, response.games().size());
    }

    @Test
    public void joinGame() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        serverFacade.createGame(new GameData(0, null, null, "MyGame", null), user.authToken());
        serverFacade.joinGame(new JoinGameData(ChessGame.TeamColor.WHITE, 1), user.authToken());

        GameData response = serverFacade.getGameDetails(1, user.authToken());
        GameData expected = new GameData(1, "Test", null, "MyGame", new ChessGame());
        Assertions.assertEquals(expected, response);
    }
    @Test
    public void colorTaken() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        serverFacade.createGame(new GameData(0, null, null, "MyGame", null), user.authToken());
        serverFacade.joinGame(new JoinGameData(ChessGame.TeamColor.WHITE, 1), user.authToken());

        HttpException thrownError = Assertions.assertThrows(
            HttpException.class, () ->    serverFacade.joinGame(new JoinGameData(ChessGame.TeamColor.WHITE, 1), user.authToken())
        );
        Assertions.assertEquals("Error: Color already taken", thrownError.getMessage());
    }

    @Test
    public void createUser() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        Assertions.assertEquals(new AuthData(user.authToken(), "Test"), user);
    }
    @Test
    public void userTaken() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        HttpException thrownError = Assertions.assertThrows(
            HttpException.class, () ->    serverFacade.createUser(new UserData("Test", "Test", "Test"))
        );
        Assertions.assertEquals("Error: account already taken", thrownError.getMessage());
    }

    @Test
    public void login() throws HttpException {
        serverFacade.createUser(new UserData("Test", "Test", "Test"));
        AuthData user = serverFacade.login(new UserData("Test", "Test", null));
        Assertions.assertEquals(new AuthData(user.authToken(), "Test"), user);
    }
    @Test
    public void invalidPassword() throws HttpException {
        serverFacade.createUser(new UserData("Test", "Test", "Test"));
        HttpException thrownError = Assertions.assertThrows(
            HttpException.class, () ->    serverFacade.login(new UserData("Test", "AHHHIMNOTVALID", null))
        );
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    public void logout() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        serverFacade.logout(user.authToken());
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   serverFacade.getGameList(user.authToken()));
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }
    @Test
    public void noUserToLogOut() throws HttpException {
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () -> serverFacade.logout("Non-Existen Id"));
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    public void clearDb() throws HttpException {
        AuthData user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        serverFacade.createGame(new GameData(0, null, null, "MyGame1", null), user.authToken());
        serverFacade.createGame(new GameData(0, null, null, "MyGame2", null), user.authToken());
        serverFacade.createGame(new GameData(0, null, null, "MyGame3", null), user.authToken());

        serverFacade.clearDb();
        user = serverFacade.createUser(new UserData("Test", "Test", "Test"));
        GameListData response = serverFacade.getGameList(user.authToken());
        Assertions.assertEquals(0, response.games().size());
    }
}
