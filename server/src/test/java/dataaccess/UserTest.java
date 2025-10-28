package dataaccess;

import dataaccess.mysqlataaccess.AuthSQLDao;
import dataaccess.mysqlataaccess.UserSQLDao;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserTest {
    private UserDao userDao = null;
    public UserTest() {
        try {
            this.userDao = new UserSQLDao();
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
        userDao.clearDb();
    }

    @Test
    void addUser() throws DataAccessException {
        userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"));
        String statement = "SELECT * FROM user";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertEquals("testUsername", result.getString("username"));
                Assertions.assertEquals("testPassword", result.getString("password"));
                Assertions.assertEquals("testEmail", result.getString("email"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    @Test
    void cannotAddDuplicateUser() throws DataAccessException {
        userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"));
        DataAccessException thrownError = Assertions.assertThrows(
            DataAccessException.class, () ->  userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"))
        );
        Assertions.assertTrue(thrownError.getMessage().contains("Duplicate entry"),
                "Error message should mention duplicate entry");
    }
    @Test
    void getPasswordHash() throws DataAccessException {
        userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"));
        String passwordHash = userDao.getPasswordHash("testUsername");
        Assertions.assertEquals("testPassword", passwordHash);
    }
    @Test
    void willReturnNullForNoUser() throws DataAccessException {
        userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"));
        String passwordHash = userDao.getPasswordHash("testBadUsername");
        Assertions.assertNull(passwordHash);
    }
    @Test
    void getUser() throws DataAccessException {
        userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"));
        UserData userData = userDao.getUser("testUsername");
        Assertions.assertEquals("testUsername", userData.username());
        Assertions.assertEquals("testPassword", userData.password());
        Assertions.assertEquals("testEmail", userData.email());
    }
    @Test
    void returnNullForNoUser() throws DataAccessException {
        UserData userData = userDao.getUser("testUsername");
        Assertions.assertNull(userData);
    }
    @Test
    void clearDb() throws DataAccessException {
        userDao.addUser(new UserData("testUsername", "testPassword", "testEmail"));
        userDao.addUser(new UserData("testUsername1", "testPassword", "testEmail"));
        userDao.addUser(new UserData("testUsername2", "testPassword", "testEmail"));

        userDao.clearDb();

        String statement = "SELECT * FROM user";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertNull(result.getString("username"));
                Assertions.assertNull(result.getString("password"));
                Assertions.assertNull(result.getString("email"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
}
