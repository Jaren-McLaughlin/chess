package service;

import model.*;
import dataaccess.*;
import chess.ChessGame.TeamColor;
import exception.HttpException;

import javax.xml.crypto.Data;
import java.util.Objects;

public class GameService {
    private final GameDao gameDao;
    public GameService (GameDao gameDao) {
        this.gameDao = gameDao;
    }
    public GameData createGame(GameData gameData) throws HttpException {
        // body: { "gameName":"" }
        // reach out to gameDao to create a game, return the gameData
        // Create a gameData record with gameID
        // response: { "gameID": 1234 }
        if (gameData.gameName() == null) {
            throw HttpException.badRequest("Error: no gameName provided");
        }
        try {
            return gameDao.addGame(gameData);
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }

    public GameListData getGameList() throws HttpException {
        // gameDao, gets all games, and returns the list of games
        // response: { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
        try {
            return gameDao.getGameList();
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }

    public void joinGame(JoinGameData joinGameData, String username) throws HttpException {
        // body: { "playerColor":"WHITE/BLACK", "gameID": 1234 }
        // call gameDao to get game data
        // check which color is taken
        // throw error if color is taken else return
        if (joinGameData.playerColor() == null) {
            throw HttpException.badRequest("Error: playerColor not provided");
        }
        if (joinGameData.gameID() == 0) {
            throw HttpException.badRequest("Error: gameId not provided");
        }
        try {
            TeamColor.valueOf(joinGameData.playerColor().toString());
        } catch (Exception error) {
            throw HttpException.badRequest("Error: invalid color");
        }

        GameData gameDetails;
        try {
            gameDetails = gameDao.getGame(joinGameData.gameID());
            System.out.println(gameDetails);
            if (gameDetails == null) {
                throw HttpException.badRequest("Error: Game not found");
            }
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
        if (
            (
                gameDetails.blackUsername() != null &&
                joinGameData.playerColor() == TeamColor.BLACK
            ) || (
                gameDetails.whiteUsername() != null &&
                joinGameData.playerColor() == TeamColor.WHITE
            )
        ) {
            throw HttpException.alreadyTaken("Error: Color already taken");
        }
        if (joinGameData.playerColor() == TeamColor.BLACK) {
            GameData newGameData = new GameData(
                    gameDetails.gameID(),
                    gameDetails.whiteUsername(),
                    username,
                    gameDetails.gameName(),
                    gameDetails.game()
            );
            try {
                gameDao.insertUserIntoGame(newGameData);
            } catch (DataAccessException error) {
                throw HttpException.badRequest("Error: Bad Request " + error);
            }
        }
        if (joinGameData.playerColor() == TeamColor.WHITE) {
            GameData newGameData = new GameData(
                    gameDetails.gameID(),
                    username,
                    gameDetails.blackUsername(),
                    gameDetails.gameName(),
                    gameDetails.game()
            );
            try {
                gameDao.insertUserIntoGame(newGameData);
            } catch (DataAccessException error) {
                throw HttpException.badRequest("Error: Bad Request " + error);
            }
        }
    }

    // Probably could be abstracted to it's only class if desired
    public void clearDb() throws HttpException {
        try {
            gameDao.clearDb();
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }
}
