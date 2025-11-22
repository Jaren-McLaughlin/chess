package dataaccess.memorydataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import dataaccess.GameDao;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMemoryDao implements GameDao {
    private final HashMap<Integer, GameData> game = new HashMap<>();
    private int numberOfGames = 1;
    private final HashMap<Integer, ChessGame.GameStatus> gameStatuses = new HashMap<>();
    public GameData addGame(GameData gameData) {
        GameData newGameData = new GameData(
                numberOfGames,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                new ChessGame()
        );
        game.put(numberOfGames, newGameData);
        gameStatuses.put(numberOfGames, ChessGame.GameStatus.PLAYING);
        numberOfGames++;
        return newGameData;
    }
    public GameData getGame(int gameId) {
        return game.get(gameId);
    }
    public GameListData getGameList() {
        return new GameListData(new ArrayList<>(game.values()));
    }
    public ChessGame.GameStatus getGameStatus(int gameId) {
        return gameStatuses.get(gameId);
    }
    public void insertUserIntoGame(GameData newData) {
        game.put(newData.gameID(), newData);
    }
    public void removeUser (int gameId, ChessGame.TeamColor teamColor) {
        GameData gameData = game.get(gameId);
        GameData newGameData;
        if (ChessGame.TeamColor.WHITE == teamColor) {
            newGameData = new GameData(
                gameId,
                null,
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
            );
        } else {
            newGameData = new GameData(
                    gameId,
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game()
            );
        }
        game.put(gameId, newGameData);
    }

    public void updateGameBoard (ChessGame chessGame, int gameId) {
        GameData gameData = game.get(gameId);
        GameData newGame = new GameData(
                gameId,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                chessGame
        );
        game.put(gameId, newGame);
    }

    public void updateGameStatus (int gameId,ChessGame.GameStatus gameStatus) {
        gameStatuses.put(gameId, gameStatus);
    }

    public void clearDb() {
        game.clear();
        numberOfGames = 1;
    }
}
