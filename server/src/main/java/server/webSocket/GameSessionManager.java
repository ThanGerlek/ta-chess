package server.webSocket;

import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import webSocketMessages.serverMessages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> gameSessions;
    private final WebSocketServer wsServer;

    // TODO Change to use GameSession instead

    public GameSessionManager(WebSocketServer wsServer) {
        this.gameSessions = new ConcurrentHashMap<>();
        this.wsServer = wsServer;
    }

    public void addUser(int gameID, String username, Session session) throws DataAccessException {
        createGameIfNeeded(gameID);
        var gameSession = gameSessions.get(gameID);
        if (gameSession == null) {
            throw new DataAccessException("Tried to add user to a GameSession that doesn't exist");
        }
        System.out.println("Registering user " + username + " with session " + session.hashCode());
        gameSession.put(username, session);
    }

    private void createGameIfNeeded(int gameID) {
        if (!gameSessions.containsKey(gameID)) {
            gameSessions.put(gameID, new ConcurrentHashMap<>());
        }
    }

    public void broadcast(int gameID, String excludedUsername, ServerMessage message) throws DataAccessException {
        var gameSession = getGameSession(gameID);
        for (String username : gameSession.keySet()) {
            if (!username.equals(excludedUsername)) {
                message(gameID, username, message);
            }
        }
    }

    public void message(int gameID, String username, ServerMessage message) throws DataAccessException {
        var gameSession = getGameSession(gameID);
        Session session = gameSession.get(username);
        try {
            wsServer.send(session, message);
        } catch (WebSocketException e) {
            if ("Session closed".equals(e.getMessage())) {
                System.err.println("Tried to send to a closed connection. Removing");
                removeUser(gameID, username);
            } else {
                throw e;
            }
        }
    }

    public void removeUser(int gameID, String username) throws DataAccessException {
        var gameSession = getGameSession(gameID);
        if (gameSession == null) {
            throw new DataAccessException("Tried to add user to a GameSession that doesn't exist");
        }
        gameSession.get(username).close();
        gameSession.remove(username);
        if (gameSession.isEmpty()) {
            gameSessions.remove(gameID);
        }
    }

    public void broadcastAll(int gameID, ServerMessage message) throws DataAccessException {
        var gameSession = getGameSession(gameID);
        for (String username : gameSession.keySet()) {
            message(gameID, username, message);
        }
    }

    public void clearGameSessions() {
        gameSessions.clear();
    }

    private ConcurrentHashMap<String, Session> getGameSession(int gameID) {
        if (!gameSessions.containsKey(gameID)) {
            throw new IllegalArgumentException(
                    String.format("Cannot call broadcast() on unrecognized gameID: %d", gameID));
        } else {
            return gameSessions.get(gameID);
        }
    }
}
