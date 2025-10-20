package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class HttpException extends Exception {
    private final int status;

    private HttpException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }

    public static HttpException badRequest(String message) {
        return new HttpException(400, message);
    }
    public static HttpException unauthorized(String message) {
        return new HttpException(401, message);
    }
    public static HttpException alreadyTaken(String message) {
        return new HttpException(403, message);
    }
    public static HttpException internalServerError(String message) {
        return new HttpException(500, message);
    }
}
// throw new HttpException(400, "Username required");