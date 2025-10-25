package service;

import model.*;
import dataaccess.*;
import java.util.UUID;
import exception.HttpException;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final AuthDao authDao;
    private final UserDao userDao;

    public UserService (AuthDao authDao, UserDao userDao) {
        this.authDao = authDao;
        this.userDao = userDao;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createUser (UserData userData) throws HttpException {
        if (userData.email() == null) {
            throw HttpException.badRequest("Error: no email");
        }
        if (userData.password() == null) {
            throw HttpException.badRequest("Error: no password");
        }
        if (userData.username() == null) {
            throw HttpException.badRequest("Error: no usernmae");
        }
        try {
            UserData userExists = userDao.getUser(userData.username());
            if(userExists != null) {
                throw HttpException.alreadyTaken("Error: account already taken");
            }
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: something went wrong " + error);
        }
        String hashPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        UserData withHash = new UserData(userData.username(), hashPassword, userData.email());
        try {
            userDao.addUser(withHash);
            return authDao.addUserAuth(generateToken(), userData.username());
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }

    public AuthData login(UserData userData) throws HttpException {
        if (userData.username() == null) {
            throw HttpException.badRequest("Error: username not found");
        }
        if (userData.password() == null) {
            throw HttpException.badRequest("Error: password not found");
        }
        String passwordHash;
        try{
            passwordHash = userDao.getPasswordHash(userData.username());
            if (passwordHash == null) {
                throw HttpException.unauthorized("Error: user not found");
            }
        } catch (DataAccessException error) {
            throw HttpException.unauthorized("Error: user not found");
        }
        if (!BCrypt.checkpw(userData.password(), passwordHash)) {
            throw HttpException.unauthorized("Error: Unauthorized");
        }
        try {
            return authDao.addUserAuth(generateToken(), userData.username());
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }

    public void logout(String authToken) throws HttpException {
        if (authToken == null) {
            throw HttpException.badRequest("Error: No authToken");
        }
        try {
            String user = authDao.getUserByToken(authToken);
            if (user == null) {
                throw HttpException.unauthorized("Error: Can't delete nonexistant");
            }
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: Something really went wrong " + error);
        }
        try{
            authDao.deleteUserAuth(authToken);
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }

    public void verifyToken (String authToken) throws HttpException {
        try {
            String username = authDao.getUserByToken(authToken);
            if (username == null) {
                throw HttpException.unauthorized("Error: Unauthorized");
            }
        } catch (DataAccessException error) {
            throw HttpException.internalServerError("Error: Something went wrong " + error);
        }
    }

    public void clearDb() throws HttpException {
        try {
            authDao.clearDb();
            userDao.clearDb();
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }
}
