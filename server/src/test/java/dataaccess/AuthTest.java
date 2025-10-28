package dataaccess;

import dataaccess.MySQLDataAccess.AuthSQLDao;
import exception.HttpException;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.*;

public class AuthTest {
    private AuthDao authDao = null;
    public AuthTest() {
        try {
            this.authDao = new AuthSQLDao();
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
        authDao.clearDb();
    }

    @Test
    void addAuth() throws DataAccessException {
        authDao.addUserAuth("test", "testUser");
        String statement = "SELECT * FROM auth";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertEquals("test", result.getString("authToken"));
                Assertions.assertEquals("testUser", result.getString("username"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    @Test
    void failOnDuplicateUser() throws DataAccessException {
        authDao.addUserAuth("test", "testUser");
        DataAccessException thrownError = Assertions.assertThrows(DataAccessException.class, () ->  authDao.addUserAuth("test", "testUser"));
        Assertions.assertTrue(thrownError.getMessage().contains("Duplicate entry"),
                "Error message should mention duplicate entry");
    }
    @Test
    void getUserByToken() throws DataAccessException {
        authDao.addUserAuth("test", "testUser");
        String username = authDao.getUserByToken("test");
        Assertions.assertEquals("testUser", username);
    }
    @Test
    void returnForNoToken() throws DataAccessException {
        authDao.addUserAuth("test", "testUser");
        String username = authDao.getUserByToken("testasdf");
        Assertions.assertNull(username);
    }
    @Test
    void deleteUserAuth() throws DataAccessException {
        authDao.addUserAuth("test", "testUser");
        authDao.deleteUserAuth("test");
        String statement = "SELECT * FROM auth";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertNull(result.getString("authToken"));
                Assertions.assertNull(result.getString("username"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    @Test
    void someNegativeDeleteAuth() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> authDao.deleteUserAuth("test"));
    }
    @Test
    void clearDb() throws DataAccessException {
        authDao.addUserAuth("test", "testUser");
        authDao.addUserAuth("test1", "testUser");
        authDao.addUserAuth("test2", "testUser");

        authDao.clearDb();

        String statement = "SELECT * FROM auth";
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            ResultSet result = query.executeQuery(statement);
            if (result.next()) {
                Assertions.assertNull(result.getString("authToken"));
                Assertions.assertNull(result.getString("username"));
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
}
