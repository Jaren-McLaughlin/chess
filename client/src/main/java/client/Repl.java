package client;

import exception.HttpException;

import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private final ServerFacade serverFacade;
    private final Session session = new Session();
    public Repl(String apiUrl) throws HttpException {
        serverFacade = new ServerFacade(apiUrl);
    }

    public void run() {
        System.out.print("Input command or type help for a list of possible options\n> ");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            CommandHandler commandHandler = session.getCommandHandler();
            String result = commandHandler.executeCommand(session, serverFacade, line);
            if (Objects.equals(result, "quit")) {
                break;
            }
            System.out.print("> ");
        }
    }
}
