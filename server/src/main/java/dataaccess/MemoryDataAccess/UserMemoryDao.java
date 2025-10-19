package dataaccess.MemoryDataAccess;

import model.UserData;
import java.util.HashMap;
import dataaccess.UserDao;

public class UserMemoryDao implements UserDao {
    private final HashMap<String, UserData> user = new HashMap<>();
    public void addUser(UserData userData) {
        user.put(userData.username(), userData);
    }
    // Get User
    public String getPasswordHash(String username) {
        UserData userDetails = user.get(username);
        return userDetails.password();
    }
    // Delete User
    public void clearDb() {
        user.clear();
    }
}
