package server;

import com.google.gson.Gson;
import exception.HttpException;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiUrl;
    public ServerFacade (String url) {
        this.apiUrl = url;
    }

    // Game
    public void createGame(GameData gameData, String authToken) throws HttpException {
        HttpRequest request = httpRequest("POST", "/game", gameData, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameListData getGameList(String authToken) throws HttpException {
        HttpRequest request = httpRequest("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, null);
    }

    public void joinGame(JoinGameData joinGameData, String authToken) throws HttpException {
        HttpRequest request = httpRequest("PUT", "/game", joinGameData, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    // User
    public AuthData createUser (UserData userData) throws HttpException {
        HttpRequest request = httpRequest("POST", "/user", userData, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, null);
    }

    public AuthData login (UserData userData) throws HttpException {
        HttpRequest request = httpRequest("POST", "/session", userData, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, null);
    }

    public void logout (String authToken) throws HttpException {
        HttpRequest request = httpRequest("DELETE", "/session", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    // DB management
    public void clearDb () throws HttpException {
        HttpRequest request = httpRequest("DELETE", "/db", null, null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    // Helper methods
    private HttpRequest httpRequest(String method, String path, Object body, String authToken) {
        HttpRequest.Builder request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl + path))
            .method(method, createBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }
    private HttpRequest.BodyPublisher createBody(Object body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
    private HttpResponse<String> sendRequest(HttpRequest request) throws HttpException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception error) {
            throw HttpException.internalServerError(error.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws HttpException {
        var status = response.statusCode();

        if (!(status/100 == 2)) {
            throw HttpException.fromJson(response.body());
        }
        if (responseClass == null) {
            return null;
        }

        return new Gson().fromJson(response.body(), responseClass);
    }
}
