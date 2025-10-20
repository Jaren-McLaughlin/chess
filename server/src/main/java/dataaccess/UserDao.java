package dataaccess;

import dataaccess.MemoryDataAccess.UserMemoryDao;
import model.*;

public interface UserDao {
    // Create user
    void addUser(UserData userData) throws DataAccessException;
    // Get User
    String getPasswordHash(String username) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    // Delete User
    void clearDb() throws DataAccessException;
}
