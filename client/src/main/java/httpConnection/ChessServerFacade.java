package httpConnection;

import chess.ChessGame;
import http.*;

import java.util.ArrayList;

public class ChessServerFacade {
    private final ServerFacade serverFacade;

    public ChessServerFacade(String serverURL) {
        this.serverFacade = new ServerFacade(serverURL);
    }

    public void clearApplication() throws FailedConnectionException, FailedResponseException {
        // Not currently available from the client. Used for testing only.
        serverFacade.makeRequest(new RequestData("DELETE", "/db"));
    }

    public AuthResponse register(String username, String password, String email)
            throws FailedConnectionException, FailedResponseException {
        RegisterRequest request = new RegisterRequest(username, password, email);

        RequestData rd = new RequestData("POST", "/user", request);
        return serverFacade.makeRequest(rd, AuthResponse.class);
    }

    public AuthResponse login(String username, String password)
            throws FailedConnectionException, FailedResponseException {
        LoginRequest request = new LoginRequest(username, password);
        RequestData rd = new RequestData("POST", "/session", request);
        return serverFacade.makeRequest(rd, AuthResponse.class);
    }

    public void logout(String authTokenString) throws FailedConnectionException, FailedResponseException {
        serverFacade.makeRequest(new RequestData("DELETE", "/session").includeToken(authTokenString));
    }

    public int createGame(String gameName, String authTokenString)
            throws FailedConnectionException, FailedResponseException {
        CreateGameRequest request = new CreateGameRequest(gameName);
        RequestData rd = new RequestData("POST", "/game", request).includeToken(authTokenString);
        CreateGameResponse response = serverFacade.makeRequest(rd, CreateGameResponse.class);
        return response.gameID();
    }

    public ArrayList<GameListItem> listGames(String authTokenString)
            throws FailedConnectionException, FailedResponseException {
        RequestData rd = new RequestData("GET", "/game").includeToken(authTokenString);
        ListGamesResponse response = serverFacade.makeRequest(rd, ListGamesResponse.class);
        return response.games();
    }

    public void joinGame(ChessGame.TeamColor playerColor, int gameID, String authTokenString)
        // TODO Change to use PlayerRole?
            throws FailedConnectionException, FailedResponseException {
        String colorString = playerColor == null ? null : playerColor.name();
        JoinGameRequest request = new JoinGameRequest(colorString, gameID);
        RequestData rd = new RequestData("PUT", "/game", request).includeToken(authTokenString);
        serverFacade.makeRequest(rd);
    }
}