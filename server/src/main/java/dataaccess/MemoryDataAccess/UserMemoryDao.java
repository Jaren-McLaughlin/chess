package dataaccess.MemoryDataAccess;

import dataaccess.DataAccessException;
import model.UserData;
import java.util.HashMap;
import dataaccess.UserDao;

import javax.xml.crypto.Data;

public class UserMemoryDao implements UserDao {
    private final HashMap<String, UserData> user = new HashMap<>();
    public void addUser(UserData userData) {
        user.put(userData.username(), userData);
    }
    // Get User
    public String getPasswordHash(String username) throws DataAccessException {
        System.out.println("Hashmap detials? " + user);
        System.out.println("Extra stuff" + username);
        UserData userDetails = user.get(username);
        System.out.println("The user details: " + userDetails);
        try {
            return userDetails.password();
        } catch (Exception error) {
            throw new DataAccessException("User not found");
        }
    }
    public UserData getUser(String username) {
        return user.get(username);
    }
    // Delete User
    public void clearDb() {
        user.clear();
    }
}
