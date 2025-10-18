package service;

import model.*;

public class UserService {
    public AuthData createUser (UserData userData) {

    }

    public AuthData login(UserData userData) {

    }

    public void logout(String authToken) {

    }

    public void verifyToken (String authToken) {
        // Reach out to authDao
        // if return is null throw no auth error
        // else returns
    }
}
