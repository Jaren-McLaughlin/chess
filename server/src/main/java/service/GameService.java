package service;

import model.*;
import dataaccess.*;
import chess.ChessGame.TeamColor;
import exception.HttpException;

public class GameService {
    private final GameDao gameDao;
    public GameService (GameDao gameDao) {
        this.gameDao = gameDao;
    }
    public GameData createGame(GameData gameData) throws HttpException {
        if (gameData.gameName() == null) {
            throw HttpException.badRequest("Error: no gameName provided");
        }
        try {
            return gameDao.addGame(gameData);
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: Internal Server Error " + error);
        }
    }

    public GameListData getGameList() throws HttpException {
        try {
            return gameDao.getGameList();
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: Internal Server Error " + error);
        }
    }

    public void joinGame(JoinGameData joinGameData, String username) throws HttpException {
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
            if (gameDetails == null) {
                throw HttpException.badRequest("Error: Game not found");
            }
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: Internal Server Error " + error);
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
                throw HttpException.internalServerError("Error: Internal Server Error " + error);
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
                throw HttpException.internalServerError("Error: Internal Server Error " + error);
            }
        }
    }

    public void clearDb() throws HttpException {
        try {
            gameDao.clearDb();
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: Internal Server Error " + error);
        }
    }
}
