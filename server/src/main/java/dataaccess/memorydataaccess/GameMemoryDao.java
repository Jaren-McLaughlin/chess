package dataaccess.memorydataaccess;

import model.*;
import dataaccess.GameDao;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMemoryDao implements GameDao {
    private final HashMap<Integer, GameData> game = new HashMap<>();
    private int numberOfGames = 1;
    public GameData addGame(GameData gameData) {
        GameData newGameData = new GameData(
                numberOfGames,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        );
        game.put(numberOfGames, newGameData);
        numberOfGames++;
        return newGameData;
    }
    public GameData getGame(int gameId) {
        return game.get(gameId);
    }
    public GameListData getGameList() {
        return new GameListData(new ArrayList<>(game.values()));
    }
    public void insertUserIntoGame(GameData newData) {
        game.put(newData.gameID(), newData);
    }
    public void clearDb() {
        game.clear();
        numberOfGames = 1;
    }
}
