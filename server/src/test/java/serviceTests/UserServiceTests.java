package serviceTests;

import dataaccess.MemoryDataAccess.AuthMemoryDao;
import dataaccess.MemoryDataAccess.GameMemoryDao;
import dataaccess.MemoryDataAccess.UserMemoryDao;
import exception.HttpException;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

public class UserServiceTests {
    private final UserService userService = new UserService(new AuthMemoryDao(), new UserMemoryDao());
    @AfterEach
    void clear() throws HttpException {
        userService.clearDb();
    }

    @Test
    void createUser() throws  HttpException {
        UserData userData = new UserData("MyUsername", "My Secure Password", "test@testing.test");
        AuthData response = userService.createUser(userData);
        Assertions.assertEquals(userData.username(), response.username());
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    void userAlreadyTaken() throws HttpException {

    }

    @Test
    void login() throws  HttpException {
        UserData userData = new UserData("MyUsername", "My Secure Password", "test@testing.test");
        userService.createUser(userData);

        AuthData response = userService.login(userData);
        Assertions.assertEquals(userData.username(), response.username());
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    void invalidLogin() throws HttpException {
        UserData userData = new UserData("MyUsername", "My Secure Password", "test@testing.test");
        userService.createUser(userData);

        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   userService.login(new UserData("MyUsername", "Bad Password", null)));
        Assertions.assertEquals(401, thrownError.getStatus());
        Assertions.assertEquals("unauthorized", thrownError.getMessage());
    }

    @Test
    void logout() throws  HttpException {

    }

    @Test
    void cannotLogOut() throws HttpException {

    }

    @Test
    void verifyToken() throws  HttpException {

    }

    @Test
    void invalidAuthToken() throws HttpException {

    }

    @Test
    void clearDb() throws  HttpException {

    }

    @Test
    void somethingNegative() throws HttpException {

    }
}
