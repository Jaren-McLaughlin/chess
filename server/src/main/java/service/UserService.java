package service;

import model.*;
import dataaccess.*;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final AuthDao authDao = new AuthDao();
    private final UserDao userDao = new UserDao();
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createUser (UserData userData) {
        // hash password
        String hashPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        // Create new record with hashed password
        UserData withHash = new UserData(userData.username(), hashPassword, userData.email());
        userDao.addUser(withHash);
        return authDao.addAuthData(userData.username(), generateToken());
    }

    public AuthData login(UserData userData) {
        String passwordHash = userDao.getPasswordHash(userData.username());
        //verify passwords match
        if (!BCrypt.checkpw(userData.password(), passwordHash)) {
            throw new Exception("ahh invalid password");
        };
        return authDao.addAuthData(userData.username(), generateToken());
    }

    public void logout(String authToken) {
        authDao.deleteUserAuth(authToken);
    }

    public void verifyToken (String authToken) {
        // Reach out to
        try {
            authDao.getUserByToken(authToken);
        } catch () {
            // not auth error.
        }
        // if return is null throw no auth error
        // else returns
    }
}
