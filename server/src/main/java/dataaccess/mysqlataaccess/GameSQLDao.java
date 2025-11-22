package dataaccess.mysqlataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDao;
import model.GameData;
import model.GameListData;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameSQLDao implements GameDao {
    public GameSQLDao() throws DataAccessException {
        DatabaseManager.createTable(gameTableSql);
    }

    public GameData addGame(GameData gameData) throws DataAccessException {
        String statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game, gameStatus) VALUES (?, ?, ?, ?, ?)";
        String jsonGame = new Gson().toJson(new ChessGame());
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, gameData.whiteUsername());
            query.setString(2, gameData.blackUsername());
            query.setString(3, gameData.gameName());
            query.setString(4, jsonGame);
            query.setString(5, ChessGame.GameStatus.PLAYING.toString());
            query.executeUpdate();
            ResultSet result = query.getGeneratedKeys();
            if (result.next()) {
                return new GameData(
                    result.getInt(1),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
                );
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
        return null;
    }

    public GameData getGame(int gameId) throws DataAccessException {
        String statement = "SELECT * FROM game WHERE gameId = ?";
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setInt(1, gameId);
            ResultSet result = query.executeQuery();
            if (result.next()) {
                ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);
                return new GameData(
                        gameId,
                        result.getString("whiteUsername"),
                        result.getString("blackUsername"),
                        result.getString("gameName"),
                        game
                );
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
        return null;
    }

    public GameListData getGameList() throws DataAccessException {
        String statement = "SELECT * FROM game";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            List<GameData> games = new ArrayList<>();
            while (result.next()) {
                ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);
                games.add(new GameData(
                        result.getInt("gameId"),
                        result.getString("whiteUsername"),
                        result.getString("blackUsername"),
                        result.getString("gameName"),
                        game
                ));
            }
            return new GameListData(games);
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    public ChessGame.GameStatus getGameStatus(int gameID) throws DataAccessException {
        String statement = "SELECT gamestatus FROM game WHERE gameId = ?";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            ChessGame.GameStatus gameStatus = null;
            while (result.next()) {
                gameStatus = ChessGame.GameStatus.valueOf(result.getString("whiteUsername"));
            }
            return gameStatus;
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    public void insertUserIntoGame(GameData newData) throws DataAccessException {
        String statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameId = ?";
        String jsonGame = new Gson().toJson(newData.game());
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setString(1, newData.whiteUsername());
            query.setString(2, newData.blackUsername());
            query.setString(3, newData.gameName());
            query.setString(4, jsonGame);
            query.setInt(5, newData.gameID());
            query.executeUpdate();
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    public void removeUser(int gameId, ChessGame.TeamColor teamColor) throws DataAccessException {
        String userColor;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            userColor = "whiteusername";
        } else {
            userColor = "blackusername";
        }
        String statement = "UPDATE game SET " + userColor + " = NULL WHERE gameId = ?";

        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setInt(1, gameId);
            query.executeUpdate();
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    public void updateGameStatus(int gameId, ChessGame.GameStatus gameStatus) throws DataAccessException {
        String statement = "UPDATE game SET gameStatus = ? WHERE gameId = ?";
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setString(1, gameStatus.toString());
            query.setInt(2, gameId);
            query.executeUpdate();
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    public void updateGameBoard(ChessGame chessGame, int gameId) throws DataAccessException {
        String jsonChessGame = new Gson().toJson(chessGame);
        String statement = "UPDATE game SET game = ? WHERE gameId = ?";

        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setString(1, jsonChessGame);
            query.setInt(2, gameId);
            query.executeUpdate();
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    public void clearDb() throws DataAccessException {
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            query.executeUpdate("TRUNCATE TABLE game");
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    private final String[] gameTableSql = {
        """
        CREATE TABLE IF NOT EXISTS game (
          `gameId` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256) NOT NULL,
          `game` JSON,
          `gameStatus` varchar(256) NOT NULL,
          PRIMARY KEY (`gameId`)
        )
        """
    };
}
