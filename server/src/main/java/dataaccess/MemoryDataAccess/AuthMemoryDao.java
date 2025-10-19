package dataaccess.MemoryDataAccess;

import java.util.HashMap;

import dataaccess.AuthDao;
import model.*;

public class AuthMemoryDao implements AuthDao {
    private final HashMap<String, String> auth = new HashMap<>();

    public AuthData addUserAuth(String username, String authToken) {
        auth.put(authToken, username);
        return new AuthData(username, authToken);
    }

    public String getUserByToken(String authToken) {
        try {
            return auth.get(authToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserAuth(String authToken) {
        auth.remove(authToken);
    }
    public void clearDb() {
        auth.clear();
    }
}
