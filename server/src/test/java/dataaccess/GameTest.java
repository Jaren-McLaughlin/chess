package dataaccess;

import dataaccess.MySQLDataAccess.AuthSQLDao;
import dataaccess.MySQLDataAccess.GameSQLDao;
import model.GameData;
import model.GameListData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameTest {
    private GameDao gameDao = null;
    public GameTest() {
        try {
            this.gameDao = new GameSQLDao();
        } catch (DataAccessException error) {
            System.out.println("Error starting DB: " + error);
        }
    }

    @BeforeAll
    static void beforeClear() throws DataAccessException {
        AuthDao authDao = new AuthSQLDao();
        authDao.clearDb();
    }
    @AfterEach
    void clear() throws DataAccessException {
        gameDao.clearDb();
    }

    @Test
    void addGame() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        String statement = "SELECT * FROM game";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertEquals(1, result.getInt("gameId"));
                Assertions.assertEquals("test", result.getString("whiteUsername"));
                Assertions.assertEquals("test2", result.getString("blackUsername"));
                Assertions.assertEquals("myGame", result.getString("gameName"));
                Assertions.assertEquals("null", result.getString("game"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    @Test
    void handlesNullInput() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                null,
                null,
                "CantBeNull",
                null
        ));
        String statement = "SELECT * FROM game";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertEquals(1, result.getInt("gameId"));
                Assertions.assertNull(result.getString("whiteUsername"));
                Assertions.assertNull(result.getString("blackUsername"));
                Assertions.assertEquals("CantBeNull", result.getString("gameName"));
                Assertions.assertEquals("null", result.getString("game"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    @Test
    void getGame() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        GameData gameData = gameDao.getGame(1);
        Assertions.assertEquals(1, gameData.gameID());
        Assertions.assertEquals("test", gameData.whiteUsername());
        Assertions.assertEquals("test2", gameData.blackUsername());
        Assertions.assertEquals("myGame", gameData.gameName());
        Assertions.assertNull(gameData.game());
    }
    @Test
    void getNullGame() throws DataAccessException {
        GameData gameData = gameDao.getGame(1);
        Assertions.assertNull(gameData);
    }
    @Test
    void getGameList() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));

        GameListData gameListData = gameDao.getGameList();
        Assertions.assertEquals(3, gameListData.games().size());
    }
    @Test
    void canGetAnEmptyList() throws DataAccessException {
        GameListData gameListData = gameDao.getGameList();
        Assertions.assertEquals(0, gameListData.games().size());
    }
    @Test
    void insertUserIntoGame() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                "test",
                null,
                "myGame",
                null
        ));
        gameDao.insertUserIntoGame(new GameData(
                1,
                "test",
                "UpdatedUsername",
                "myGame",
                null
        ));

        String statement = "SELECT * FROM game";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertEquals(1, result.getInt("gameId"));
                Assertions.assertEquals("test", result.getString("whiteUsername"));
                Assertions.assertEquals("UpdatedUsername", result.getString("blackUsername"));
                Assertions.assertEquals("myGame", result.getString("gameName"));
                Assertions.assertEquals("null", result.getString("game"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    @Test
    void someNegativeInsert() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        DataAccessException thrownError = Assertions.assertThrows(DataAccessException.class, () -> gameDao.insertUserIntoGame(new GameData(
                1,
                "test",
                "test2",
                null,
                null
        )));

        Assertions.assertTrue(thrownError.getMessage().contains("Column 'gameName' cannot be null"));
    }
    @Test
    void clearDb() throws DataAccessException {
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));
        gameDao.addGame(new GameData(
                0,
                "test",
                "test2",
                "myGame",
                null
        ));

        gameDao.clearDb();

        String statement = "SELECT * FROM game";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertNull(result.getString("gameId"));
                Assertions.assertNull(result.getString("whiteUsername"));
                Assertions.assertNull(result.getString("blackUsername"));
                Assertions.assertNull(result.getString("gameName"));
                Assertions.assertNull(result.getString("game"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
}