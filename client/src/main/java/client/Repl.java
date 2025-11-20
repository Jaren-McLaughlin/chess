package client;

import exception.HttpException;

import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private final ServerFacade serverFacade;
    private final ClientSession clientSession = new ClientSession();
    public Repl(String apiUrl) throws HttpException {
        serverFacade = new ServerFacade(apiUrl);
        clientSession.setApiUrl(apiUrl);
//        System.out.println(clientSession.getApiUrl());
        clientSession.setCommandHandler(new GamePlay(clientSession));
    }

    public void run() {
        System.out.print("Input command or type help for a list of possible options\n> ");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            CommandHandler commandHandler = clientSession.getCommandHandler();
            String result = commandHandler.executeCommand(clientSession, serverFacade, line);
            if (Objects.equals(result, "quit")) {
                break;
            }
            System.out.print("> ");
        }
    }
}
