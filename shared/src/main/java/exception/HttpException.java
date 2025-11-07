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

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", status));
    }

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

    public static HttpException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = ((Number) map.get("status")).intValue();
        String message = map.get("message").toString();
        return new HttpException(status, message);
    }
}