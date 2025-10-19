package service;

import model.*;
import dataaccess.*;
import chess.ChessGame.TeamColor;
import exception.HttpException;
import java.util.Objects;

public class GameService {
    private final GameDao gameDao = new GameDao();
    public GameData createGame(GameData gameData) throws HttpException {
        // body: { "gameName":"" }
        // reach out to gameDao to create a game, return the gameData
        // Create a gameData record with gameID
        // response: { "gameID": 1234 }
        return gameDao.addGame(gameData);
    }

    public GameListData getGameList() throws HttpException {
        // gameDao, gets all games, and returns the list of games
        // response: { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
        return gameDao.getGameList();
    }

    public void joinGame(JoinGameData joinGameData, String username) throws HttpException {
        // body: { "playerColor":"WHITE/BLACK", "gameID": 1234 }
        // call gameDao to get game data
        // check which color is taken
        // throw error if color is taken else return
        GameData gameDetails = gameDao.getGame(joinGameData.gameID());
        if (
            (
                gameDetails.blackUsername() != null &&
                joinGameData.playerColor() == TeamColor.BLACK
            ) || (
                gameDetails.whiteUsername() != null &&
                joinGameData.playerColor() == TeamColor.WHITE
            )
        ) {
            throw HttpException.alreadyTaken("Color already taken");
        }
        if (joinGameData.playerColor() == TeamColor.BLACK) {
            GameData newGameData = new GameData(
                    gameDetails.gameID(),
                    gameDetails.whiteUsername(),
                    username,
                    gameDetails.gameName(),
                    gameDetails.game()
            );
            gameDao.insertIntoGame(newGameData);
        }
        if (joinGameData.playerColor() == TeamColor.WHITE) {
            GameData newGameData = new GameData(
                    gameDetails.gameID(),
                    username,
                    gameDetails.blackUsername(),
                    gameDetails.gameName(),
                    gameDetails.game()
            );
            gameDao.insertIntoGame(newGameData);
        }
    }

    // Probably could be abstracted to it's only class if desired
    public void clearDb() throws HttpException {
        gameDao.deleteGameTableRecords();
    }
}
