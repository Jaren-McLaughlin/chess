package service;

import model.*;
import dataaccess.*;
import java.util.Objects;
import java.util.UUID;
import exception.HttpException;
//import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final AuthDao authDao;
    private final UserDao userDao;

    public UserService (AuthDao authDao, UserDao userDao) {
        this.authDao = authDao;
        this.userDao = userDao;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createUser (UserData userData) throws HttpException {
        // hash password (Commented out cause I don't know why the import doesn't work)
//        String hashPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        // Create new record with hashed password
//        UserData withHash = new UserData(userData.username(), hashPassword, userData.email());
        try {
            userDao.addUser(userData);
            return authDao.addUserAuth(userData.username(), generateToken());
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Bad Request: " + error);
        }
    }

    public AuthData login(UserData userData) throws HttpException {
        String passwordHash;
        try{
            passwordHash = userDao.getPasswordHash(userData.username());
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Bad Request: " + error);
        }
        //verify passwords match
//        if (!BCrypt.checkpw(userData.password(), passwordHash)) {
        if (Objects.equals(userData.password(), passwordHash)) {
            throw HttpException.unauthorized("unauthorized");
        };
        try {
            return authDao.addUserAuth(userData.username(), generateToken());
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Bad Request: " + error);
        }
    }

    public void logout(String authToken) throws HttpException {
        try{
            authDao.deleteUserAuth(authToken);
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Bad Request: " + error);
        }
    }

    public void verifyToken (String authToken) throws HttpException {
        // Reach out to
        try {
            authDao.getUserByToken(authToken);
        } catch (DataAccessException error) {
            // not auth error.
            throw HttpException.unauthorized("Unauthorized");
        }
        // if return is null throw no auth error
        // else returns
    }

    public void clearDb() throws HttpException {
        try {
            authDao.clearDb();
            userDao.clearDb();
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Bad Request: " + error);
        }
    }
}
