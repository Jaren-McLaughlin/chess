package dataaccess;

import dataaccess.MemoryDataAccess.GameMemoryDao;
import model.*;
import chess.ChessGame.TeamColor;

import java.util.Objects;

public class GameDao {
    private final GameMemoryDao gameMemoryDao = new GameMemoryDao();
    // Create Game
    public GameData addGame(GameData gameData) {
        return gameMemoryDao.addGame(gameData);
    }
    // Get Game
    public GameData getGame(int gameId) {
        return gameMemoryDao.getGame(gameId);
    }
    // List game
    public GameListData getGameList() {
        return gameMemoryDao.getGameList();
    }
    // Update game
    public void insertIntoGame(GameData updateGameData) {
        gameMemoryDao.insertUserIntoGame(updateGameData);
    }

    public void deleteGameTableRecords() {
        gameMemoryDao.clearDb();
    }
}
