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

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createUser (UserData userData) throws HttpException {
        // hash password (Commented out cause I don't know why the import doesn't work)
//        String hashPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        // Create new record with hashed password
//        UserData withHash = new UserData(userData.username(), hashPassword, userData.email());
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
        try {
            userDao.addUser(userData);
            return authDao.addUserAuth(generateToken(), userData.username());
        } catch (DataAccessException error) {
            throw HttpException.badRequest("Error: Bad Request " + error);
        }
    }

    public AuthData login(UserData userData) throws HttpException {
        if (userData.username() == null) {
            System.out.println("Made it in to throw an error");
            throw HttpException.badRequest("Error: username not found");
        }
        if (userData.password() == null) {
            throw HttpException.badRequest("Error: password not found");
        }
        System.out.println("This should be called and do something " + userData);
        String passwordHash;
        try{
            passwordHash = userDao.getPasswordHash(userData.username());
            if (passwordHash == null) {
                throw HttpException.unauthorized("Error: user not found");
            }
        } catch (DataAccessException error) {
            System.out.println("was this called");
            throw HttpException.unauthorized("Error: user not found");
        }
        System.out.println("Should have errored out " + passwordHash);
        //verify passwords match
//        if (!BCrypt.checkpw(userData.password(), passwordHash)) {
        if (!Objects.equals(userData.password(), passwordHash)) {
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
        // Reach out to
        System.out.println("Verify is called?");
        try {
            String username = authDao.getUserByToken(authToken);
            if (username == null) {
                throw HttpException.unauthorized("Error: Unauthorized");
            }
        } catch (DataAccessException error) {
            // not auth error.
            throw HttpException.internalServerError("Error: Something went wrong " + error);
        }
        // if return is null throw no auth error
        // else returns
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
