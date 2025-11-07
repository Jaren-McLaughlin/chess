import exception.HttpException;
import client.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");

        String apiUrl = "http://localhost:8080";
        if (args.length == 1) {
            apiUrl = args[0];
        }
        try {
            new Repl(apiUrl).run();
        } catch (HttpException error) {
            System.out.println("There was an error " + error);
        }
    }
}