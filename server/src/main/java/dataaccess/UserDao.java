package dataaccess;

import dataaccess.MemoryDataAccess.UserMemoryDao;
import exception.HttpException;
import model.*;

public class UserDao {
    private final UserMemoryDao userMemoryDao = new UserMemoryDao();
    // Create user
    public void addUser(UserData userData) throws HttpException {
        userMemoryDao.addUser(userData);
    }
    // Get User
    public String getPasswordHash(String username) throws HttpException {
        return userMemoryDao.getPasswordHash(username);
    }
    // Delete User
    public void deleteUserTableRecords() throws HttpException {
        userMemoryDao.clearDb();
    }
}
