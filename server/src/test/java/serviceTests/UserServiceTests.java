package serviceTests;

import dataaccess.MemoryDataAccess.AuthMemoryDao;
import dataaccess.MemoryDataAccess.UserMemoryDao;
import exception.HttpException;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
        UserData userData = new UserData("MyUsername", "My Secure Password", "test@testing.test");
        userService.createUser(userData);
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   userService.createUser(userData));
        Assertions.assertEquals(403, thrownError.getStatus());
        Assertions.assertEquals("Error: account already taken", thrownError.getMessage());
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
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    void logout() throws HttpException {
        UserData userData = new UserData("username", "passowrd", "email");
        AuthData authData = userService.createUser(userData);
        userService.logout(authData.authToken());
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   userService.verifyToken(authData.authToken()));
        Assertions.assertEquals(401, thrownError.getStatus());
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    void cannotLogOut() throws HttpException {
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   userService.logout("10231"));
        Assertions.assertEquals(401, thrownError.getStatus());
        Assertions.assertEquals("Error: Can't delete nonexistant", thrownError.getMessage());
    }

    @Test
    void verifyToken() throws  HttpException {
        AuthData authData = userService.createUser(new UserData("test", "test", "test"));
        Assertions.assertDoesNotThrow(() -> userService.verifyToken(authData.authToken()));
    }

    @Test
    void invalidAuthToken() throws HttpException {
        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   userService.verifyToken("10231"));
        Assertions.assertEquals(401, thrownError.getStatus());
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }

    @Test
    void clearDb() throws  HttpException {
        AuthData user = userService.createUser(new UserData("test", "test", "test"));

        userService.clearDb();

        HttpException thrownError = Assertions.assertThrows(HttpException.class, () ->   userService.verifyToken(user.authToken()));
        Assertions.assertEquals(401, thrownError.getStatus());
        Assertions.assertEquals("Error: Unauthorized", thrownError.getMessage());
    }
}
