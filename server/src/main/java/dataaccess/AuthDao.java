package dataaccess;

import model.*;

public interface AuthDao {
    // Create Auth
    AuthData addUserAuth(String authToken, String username) throws DataAccessException;
    // Verify Auth
    String getUserByToken(String authToken) throws DataAccessException;
    // Delete Auth
    void deleteUserAuth(String authToken) throws DataAccessException;

    public void clearDb() throws DataAccessException;
}
