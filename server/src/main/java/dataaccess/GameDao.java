package dataaccess;

import dataaccess.MemoryDataAccess.GameMemoryDao;
import exception.HttpException;
import model.*;
import chess.ChessGame.TeamColor;

import java.util.Objects;

public class GameDao {
    private final GameMemoryDao gameMemoryDao = new GameMemoryDao();
    // Create Game
    public GameData addGame(GameData gameData) throws HttpException {
        return gameMemoryDao.addGame(gameData);
    }
    // Get Game
    public GameData getGame(int gameId) throws HttpException {
        return gameMemoryDao.getGame(gameId);
    }
    // List game
    public GameListData getGameList() throws HttpException {
        return gameMemoryDao.getGameList();
    }
    // Update game
    public void insertIntoGame(GameData updateGameData) throws HttpException {
        gameMemoryDao.insertUserIntoGame(updateGameData);
    }

    public void deleteGameTableRecords() throws HttpException {
        gameMemoryDao.clearDb();
    }
}
