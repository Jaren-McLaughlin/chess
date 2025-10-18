package dataaccess.MemoryDataAccess;

import java.util.HashMap;

public class AuthMemoryDao {
    private final HashMap<String, String> auth = new HashMap<>();

    public void addUserAuth(String username, String authToken) {
        auth.put(authToken, username);
    }

    public void getUserByToken(String authToken) {
        try {
            String username = auth.get(authToken);
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
