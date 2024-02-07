package client;

import chess.ChessGame;

public class SessionData {
    private AuthorizationRole authRole;
    private String authTokenString;
    private String username;
    private int gameID;
    private ChessGame.TeamColor playerColor;
    private boolean isInGame;

    public SessionData() {
        this(AuthorizationRole.GUEST, null, null);
    }

    public SessionData(AuthorizationRole authRole, String authTokenString, String username) {
        this.authRole = authRole;
        this.authTokenString = authTokenString;
        this.username = username;
    }

    public AuthorizationRole getAuthRole() {
        return authRole;
    }

    public void setAuthRole(AuthorizationRole authRole) {
        this.authRole = authRole;
    }

    public void clearUserData() {
        setUserData(null, null);
        this.authRole = AuthorizationRole.GUEST;
    }

    public void setUserData(String authTokenString, String username) {
        this.authTokenString = authTokenString;
        this.username = username;
    }

    public String getAuthTokenString() {
        return authTokenString;
    }

    public String getUsername() {
        return username;
    }

    public void setGameData(int gameID, ChessGame.TeamColor playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
        isInGame = true;
    }

    public void clearGameData() {
        gameID = -1;
        playerColor = null;
        isInGame = false;
    }

    public int getGameID() {
        requireInGame();
        return gameID;
    }

    private void requireInGame() {
        if (!isInGame) {
            throw new NullPointerException("Tried to get game data when not in a game");
        }
    }

    public ChessGame.TeamColor getPlayerColor() {
        requireInGame();
        return playerColor;
    }

    public boolean isInGame() {
        return isInGame;
    }
}
