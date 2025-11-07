package ui;

import exception.HttpException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class PreLogin implements CommandHandler {
    public String executeCommand(Session session, ServerFacade serverFacade, String input) {
        String[] values = input.toLowerCase().split(" ");
        String command = (values.length > 0) ? values[0] : "help";
        String[] parameters = Arrays.copyOfRange(values, 1, values.length);
        return switch (command) {
            case "help" -> help();
            case "login" -> login(session, serverFacade, parameters);
            case "quit" -> quit();
            case "register" -> register(session, serverFacade, parameters);
            default -> unknownCommand(command);
        };
    }

    private String help() {
        String helpPrompt = """
            Logged Out Commands:
            help - Shows available commands
            login <Username> <Password> - Log into your account
            quit - End your connection
            register <Username> <Password> <Email> - Create an account
        """;
        System.out.println(helpPrompt);
        return null;
    }
    private String login(Session session, ServerFacade serverFacade, String[] input) {
        if (input.length < 2) {
            System.out.println("Invalid options for login, please follow this format: login <Username> <Password>");
            return null;
        }
        AuthData response;
        try {
            response = serverFacade.login(new UserData(input[0], input[1], null));
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        session.setAuthToken(response.authToken());
        session.setCommandHandler(new PostLogin());
        System.out.println("Successfully logged in, type \"help\" to view a list of logged in commands");
        return "Success";
    }
    private String quit() {
        System.out.println("Closing connection, goodbye.");
        return "quit";
    }
    private String register(Session session, ServerFacade serverFacade, String[] input) {
        if (input.length < 3) {
            System.out.println("Invalid options for registering an account, please follow this format: register <Username> <Password> <Email>");
            return null;
        }
        AuthData response;
        try {
            response = serverFacade.createUser(new UserData(input[0], input[1], input[2]));
        } catch (HttpException error) {
            System.out.println(error.getMessage());
            return null;
        } catch (IllegalArgumentException error) {
            System.out.println("Error: Invalid parameter provided");
            return null;
        }
        session.setAuthToken(response.authToken());
        session.setCommandHandler(new PostLogin());
        System.out.println("Successfully created account! Type \"help\" to view the logged in commands");
        return "Success";
    }
    private String unknownCommand(String input) {
        System.out.println("Unknown Command: " + input + "\nType \"help\" for a list of logged out commands");
        return null;
    }
}
