package dataaccess;

import dataaccess.MemoryDataAccess.AuthMemoryDao;
import model.*;

public class AuthDao {
    private final AuthMemoryDao authMemoryDao = new AuthMemoryDao();
    // Create Auth
    public AuthData addAuthData(String username, String authToken) {
        authMemoryDao.addUserAuth(authToken, username);
        return new AuthData(username, authToken);
    }
    // Verify Auth
    public void getUserByToken(String authToken) {
        try {
            authMemoryDao.getUserByToken(authToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // Delete Auth
    public void deleteUserAuth(String authToken) {
        authMemoryDao.deleteUserAuth(authToken);
    }

    public void deleteAuthTableRecords() {
        authMemoryDao.clearDb();
    }
}
