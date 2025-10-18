package service;

import model.*;
import dataaccess.*;
import chess.ChessGame.TeamColor;

import java.util.Objects;

public class GameService {
    private final GameDao gameDao = new GameDao();
    public GameData createGame(GameData gameData) {
        // body: { "gameName":"" }
        // reach out to gameDao to create a game, return the gameData
        // Create a gameData record with gameID
        // response: { "gameID": 1234 }
        return gameDao.addGame(gameData);
    }

    public GameListData getGameList() {
        // gameDao, gets all games, and returns the list of games
        // response: { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
        return gameDao.getGameList();
    }

    public void joinGame(JoinGameData joinGameData) {
        // body: { "playerColor":"WHITE/BLACK", "gameID": 1234 }
        // call gameDao to get game data
        // check which color is taken
        // throw error if color is taken else return
        GameData gameDetails = gameDao.getGame(joinGameData.gameID());
        if (
            (
                gameDetails.blackUsername() != null &&
                Objects.equals(joinGameData.playerColor(), TeamColor.BLACK.toString())) ||
            (
                gameDetails.whiteUsername() != null &&
                Objects.equals(joinGameData.playerColor(), TeamColor.WHITE.toString()))
        ) {
            throw new ResponseException();
        }
        gameDao.addToGame(joinGameData);
    }

    // Probably could be abstracted to it's only class if desired
    public void clearDb() {
        // call each delete dao
        authDao.clearTable();
        gameDao.clearTable();
        userDao.clearTable();
    }
}
