package dataaccess;

import dataaccess.MySQLDataAccess.AuthSQLDao;
import dataaccess.MySQLDataAccess.GameSQLDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    }
    @Test
    void someNegativeAddGame() throws DataAccessException {

    }
    @Test
    void getGame() throws DataAccessException {

    }
    @Test
    void someNegativeGetGame() throws DataAccessException {

    }
    @Test
    void getGameList() throws DataAccessException {

    }
    @Test
    void someNegativeGetGameList() throws DataAccessException {

    }
    @Test
    void insertUserIntoGame() throws DataAccessException {

    }
    @Test
    void someNegativeInsert() throws DataAccessException {

    }
    @Test
    void clearDb() throws DataAccessException {

    }
}