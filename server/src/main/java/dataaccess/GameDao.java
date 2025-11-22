package dataaccess;

import chess.ChessGame;
import model.*;
import chess.ChessGame.TeamColor;

import java.util.Objects;

public interface GameDao {
    // Create Game
    GameData addGame(GameData gameData) throws DataAccessException;
    // Get Game
    GameData getGame(int gameId) throws DataAccessException;
    // List game
    GameListData getGameList() throws DataAccessException;
    ChessGame.GameStatus getGameStatus (int gameID) throws DataAccessException;
    // Update game
    void insertUserIntoGame(GameData updateGameData) throws DataAccessException;

    void removeUser(int gameId, TeamColor teamColor) throws DataAccessException;

    void updateGameBoard(ChessGame chessGame, int gameId) throws DataAccessException;

    void updateGameStatus(int gameId, ChessGame.GameStatus gameStatus) throws DataAccessException;

    void clearDb() throws DataAccessException;
}
