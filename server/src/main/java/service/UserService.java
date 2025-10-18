package service;

import model.*;
import dataaccess.*;

public class UserService {
    private final AuthDao authDao = new AuthDao();
    private final UserDao userDao = new UserDao();
    public AuthData createUser (UserData userData) {
        userDao.createUser(userData);
        return authDao.createAuthToken(userData.username());
    }

    public AuthData login(UserData userData) {
        userDao.getUserDetails(userData);
        //verify passwords match

        return authDao.createAuthToken(userData.username());
    }

    public void logout(String authToken) {
        authDao.deleteAuthToken(authToken);
    }

    public void verifyToken (String authToken) {
        // Reach out to
        try {
            authDao.getAuthToken();
        } catch () {
            // not auth error.
        }
        // if return is null throw no auth error
        // else returns
    }
}
