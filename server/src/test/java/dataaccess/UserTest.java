package dataaccess;

import dataaccess.MySQLDataAccess.GameSQLDao;
import dataaccess.MySQLDataAccess.UserSQLDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    private UserDao userDao = null;
    public UserTest() {
        try {
            this.userDao = new UserSQLDao();
        } catch (DataAccessException error) {
            System.out.println("Error starting DB: " + error);
        }
    }

    @AfterEach
    void clear() throws DataAccessException {
        userDao.clearDb();
    }

    @Test
    void addUser() throws DataAccessException {

    }
    @Test
    void someNegativeAddUser() throws DataAccessException {

    }
    @Test
    void getPasswordHash() throws DataAccessException {

    }
    @Test
    void someNegativeGetPasswordHash() throws DataAccessException {

    }
    @Test
    void getUser() throws DataAccessException {

    }
    @Test
    void someNegativeGetUser() throws DataAccessException {

    }
    @Test
    void clearDb() throws DataAccessException {

    }
}
