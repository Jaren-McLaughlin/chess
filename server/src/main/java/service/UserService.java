package service;

import model.*;
import dataaccess.*;
import java.util.UUID;

public class UserService {
    private final AuthDao authDao = new AuthDao();
    private final UserDao userDao = new UserDao();
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createUser (UserData userData) {
        // hash password
        String hashPassword = ;
        // Create new record with hashed password
        UserData withHash = new UserData(userData.username(), hashPassword, userData.email());
        userDao.addUser(userData);
        return authDao.addAuthData(userData.username(), generateToken());
    }

    public AuthData login(UserData userData) {
        userDao.getUserDetails(userData);
        //verify passwords match

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
