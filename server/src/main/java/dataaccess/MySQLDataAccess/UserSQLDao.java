package dataaccess.MySQLDataAccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDao;
import model.UserData;

import java.sql.*;

public class UserSQLDao implements UserDao {
    public UserSQLDao() throws DataAccessException {
        createTable();
    }

    public void addUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO user (email, password, username) VALUES (?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setString(1, userData.email());
            query.setString(2, userData.password());
            query.setString(3, userData.username());
            query.executeUpdate();
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
    public String getPasswordHash(String username) throws DataAccessException {
        String statement = "SELECT password FROM user WHERE username = ?";
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setString(1, username);
            ResultSet result = query.executeQuery();
            if (result.next()) {
                return result.getString("password");
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
        return null;
    }
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT * FROM user WHERE username = ?";
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement query = con.prepareStatement(statement);
            query.setString(1, username);
            ResultSet result = query.executeQuery();
            if (result.next()) {
                return new UserData(
                    result.getString("email"),
                    result.getString("password"),
                    result.getString("username")
                );
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
        return null;
    }

    public void clearDb() throws DataAccessException {
        try (Connection con = DatabaseManager.getConnection()) {
            Statement query = con.createStatement();
            query.executeUpdate("TRUNCATE TABLE user");
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }

    private final String[] userTableSql = {
        """
        CREATE TABLE IF NOT EXISTS  `user` (
          `email` varchar(256),
          `password` varchar(256) NOT NULL,
          `username` varchar(256) NOT NULL,
          PRIMARY KEY (`username`)
        )
        """
    };

    private void createTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection con = DatabaseManager.getConnection()) {
            for (String statement : userTableSql) {
                try (var preparedStatement = con.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException error) {
            throw new DataAccessException("SQL Error: " + error);
        }
    }
}
