package dataaccess;

import dataaccess.MemoryDataAccess.AuthMemoryDao;
import exception.HttpException;
import model.*;

public class AuthDao {
    private final AuthMemoryDao authMemoryDao = new AuthMemoryDao();
    // Create Auth
    public AuthData addAuthData(String username, String authToken) throws HttpException {
        authMemoryDao.addUserAuth(authToken, username);
        return new AuthData(username, authToken);
    }
    // Verify Auth
    public String getUserByToken(String authToken) throws HttpException {
        try {
            return authMemoryDao.getUserByToken(authToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // Delete Auth
    public void deleteUserAuth(String authToken) throws HttpException {
        authMemoryDao.deleteUserAuth(authToken);
    }

    public void deleteAuthTableRecords() throws HttpException {
        authMemoryDao.clearDb();
    }
}
