
import server.Server;
import dataaccess.*;
import dataaccess.MemoryDataAccess.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        AuthDao authDao = new AuthMemoryDao();
        GameDao gameDao = new GameMemoryDao();
        UserDao userDao = new UserMemoryDao();
        GameService gameService = new GameService(gameDao);
        UserService userService = new UserService(authDao, userDao);
        Server server = new Server();
        server.addServices(gameService, userService, authDao);
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}