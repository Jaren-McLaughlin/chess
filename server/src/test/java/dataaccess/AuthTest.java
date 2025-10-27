package dataaccess;

import dataaccess.MySQLDataAccess.AuthSQLDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AuthTest {
    private AuthDao authDao = null;
    public AuthTest() {
        try {
            this.authDao = new AuthSQLDao();
        } catch (DataAccessException error) {
            System.out.println("Error starting DB: " + error);
        }
    }

    @AfterEach
    void clear() throws DataAccessException {
        authDao.clearDb();
    }

    @Test
    void addAuth() throws DataAccessException {

    }
    @Test
    void someNegativeAddAuth() throws DataAccessException {

    }
    @Test
    void getUserByToken() throws DataAccessException {

    }
    @Test
    void someNegativeGetAuth() throws DataAccessException {

    }
    @Test
    void deleteUserAuth() throws DataAccessException {

    }
    @Test
    void someNegativeDeleteAuth() throws DataAccessException {

    }
    @Test
    void clearDb() throws DataAccessException {

    }
}
