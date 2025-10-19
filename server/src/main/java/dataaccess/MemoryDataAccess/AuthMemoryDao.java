package dataaccess.MemoryDataAccess;

import java.util.HashMap;

public class AuthMemoryDao {
    private final HashMap<String, String> auth = new HashMap<>();

    public void addUserAuth(String username, String authToken) {
        auth.put(authToken, username);
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
