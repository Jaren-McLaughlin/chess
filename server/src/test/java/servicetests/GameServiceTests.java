package servicetests;

import chess.ChessGame.TeamColor;
import dataaccess.memorydataaccess.GameMemoryDao;
import exception.HttpException;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;


public class GameServiceTests {

    private final GameService gameService = new GameService(new GameMemoryDao());
    @AfterEach
    void clear() throws HttpException {
        gameService.clearDb();
    }
    @Test
    void createGame() throws HttpException {
        GameData request = new GameData(0, null, null, "My Test game", null);
        GameData response = gameService.createGame(request);
        Assertions.assertEquals(1, response.gameID());

        GameListData gameDataList = gameService.getGameList();
        Assertions.assertEquals(1, gameDataList.games().size());
        Assertions.assertTrue(gameDataList.games().contains(new GameData(response.gameID(), null, null, request.gameName(), null)));
    }

    @Test
    void noGameName() throws HttpException {
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   gameService.createGame(new GameData(0, null, null, null, null
        )));
        Assertions.assertEquals(400, thrownError.getStatus());
        Assertions.assertEquals("Error: no gameName provided", thrownError.getMessage());
    }

    @Test
    void getGameList() throws HttpException {
        gameService.createGame(new GameData(0, null, null, "My first game", null));
        gameService.createGame(new GameData(0, null, null, "My Second game", null));
        gameService.createGame(new GameData(0, null, null, "My Third game", null));

        GameListData gameDataList = gameService.getGameList();
        Assertions.assertEquals(3, gameDataList.games().size());
        Assertions.assertTrue(gameDataList.games().contains(new GameData(1, null, null, "My first game", null)));
        Assertions.assertTrue(gameDataList.games().contains(new GameData(2, null, null, "My Second game", null)));
        Assertions.assertTrue(gameDataList.games().contains(new GameData(3, null, null, "My Third game", null)));

    }

    @Test
    void joinGame() throws HttpException {
        GameData request = new GameData(0, null, null, "My Test game", null);
        GameData response = gameService.createGame(request);

        gameService.joinGame(new JoinGameData(TeamColor.WHITE, response.gameID()), "My Username");

        GameListData gameList = gameService.getGameList();
        Assertions.assertTrue(gameList.games().contains(new GameData(response.gameID(), "My Username", null, response.gameName(), null)));
    }

    @Test
    void joinGameColorTaken() throws HttpException {
        GameData request = new GameData(0, "TakenColor", null, "My Test game", null);
        GameData response = gameService.createGame(request);

        Assertions.assertThrows(HttpException.class, () -> gameService.joinGame(new JoinGameData(TeamColor.WHITE, response.gameID()), "My Username"));
    }

    @Test
    void clearDb() throws HttpException {
        gameService.createGame(new GameData(0, null, null, "My first game", null));
        gameService.createGame(new GameData(0, null, null, "My Second game", null));
        gameService.createGame(new GameData(0, null, null, "My Third game", null));

        gameService.clearDb();
        GameListData gameList = gameService.getGameList();
        Assertions.assertEquals(0, gameList.games().size());
    }
}
