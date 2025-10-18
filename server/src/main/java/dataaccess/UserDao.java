package dataaccess;

import dataaccess.MemoryDataAccess.UserMemoryDao;
import model.*;

public class UserDao {
    private final UserMemoryDao userMemoryDao = new UserMemoryDao();
    // Create user
    public void addUser(UserData userData) {
        userMemoryDao.addUser(userData);
    }
    // Get User
    public String getPasswordHash(String username) {
        return userMemoryDao.getPasswordHash(username);
    }
    // Delete User
    public void deleteUserTableRecords() {
        userMemoryDao.clearDb();
    }
}
