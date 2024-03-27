package server.webSocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import chess.WinState;
import dataAccess.*;
import http.ChessSerializer;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGameServerMessage;
import webSocketMessages.serverMessages.NotificationServerMessage;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;
import java.util.Objects;

public class UserGameCommandHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final GameSessionManager sessionManager;
    private final WebSocketServer wsServer;

    public UserGameCommandHandler(AuthDAO authDAO, GameDAO gameDAO, WebSocketServer wsServer) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.sessionManager = new GameSessionManager(wsServer);
        this.wsServer = wsServer;
    }

    public void parseAsJoinObserver(Session session, String message) throws DataAccessException {
        JoinObserverGameCommand gameCommand = ChessSerializer.gson().fromJson(message, JoinObserverGameCommand.class);
        System.out.printf("JOIN_OBSERVER | gameID: %d%n", gameCommand.getGameID());

        requireValidAuthString(gameCommand);

        String username = authDAO.getUsername(gameCommand.getAuthString());
        int gameID = gameCommand.getGameID();
        Game game = gameDAO.findGame(gameID);

        gameDAO.assignPlayerRole(gameID, username, PlayerRole.SPECTATOR);
        sessionManager.addUser(gameID, username, session);

        LoadGameServerMessage loadGameMsg = new LoadGameServerMessage(game);
        wsServer.send(session, loadGameMsg);

        String notifyStr = String.format("User %s has joined the game as an observer", username);
        NotificationServerMessage notifyMsg = new NotificationServerMessage(notifyStr);
        sessionManager.broadcast(gameID, username, notifyMsg);
    }

    private void requireValidAuthString(UserGameCommand gameCommand) throws DataAccessException {
        if (!authDAO.isValidAuthToken(gameCommand.getAuthString())) {
            throw new UnauthorizedAccessException("Invalid token provided");
        }
    }

    private void requireHasRoleInGame(String authString, int gameID) throws DataAccessException {
        String username = authDAO.getUsername(authString);
        Game game = gameDAO.findGame(gameID);
        ArrayList<String> observers = game.getSpectators();
        if (!Objects.equals(username, game.blackUsername()) && !Objects.equals(username, game.whiteUsername()) &&
                !observers.contains(username)) {
            String msg = String.format("Call to requireUserInGame() failed to find a role for user '%s' in game %d",
                    username, gameID);
            throw new BadRequestException(msg);
        }
    }

    public void parseAsJoinPlayer(Session session, String message) throws DataAccessException {
        JoinPlayerGameCommand gameCommand = ChessSerializer.gson().fromJson(message, JoinPlayerGameCommand.class);
        System.out.printf("JOIN_PLAYER | gameID: %d, color: %s%n", gameCommand.getGameID(),
                gameCommand.getPlayerColor().name());

        requireValidAuthString(gameCommand);

        String username = authDAO.getUsername(gameCommand.getAuthString());
        int gameID = gameCommand.getGameID();
        Game game = gameDAO.findGame(gameID);

        PlayerRole role;
        String assignedUsername;
        if (gameCommand.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            role = PlayerRole.WHITE_PLAYER;
            assignedUsername = game.whiteUsername();
        } else {
            role = PlayerRole.BLACK_PLAYER;
            assignedUsername = game.blackUsername();
        }

        if (!Objects.equals(username, assignedUsername)) {
            wsServer.sendError(session, "Sorry, that role is already taken.");
            return;
        }

        sessionManager.addUser(gameID, username, session);
        gameDAO.assignPlayerRole(gameID, username, role);

        LoadGameServerMessage loadGameMsg = new LoadGameServerMessage(game);
        wsServer.send(session, loadGameMsg);

        String roleString = PlayerRole.roleToString(role);
        String notifyStr = String.format("User %s has joined the game as %s", username, roleString);
        NotificationServerMessage notifyMsg = new NotificationServerMessage(notifyStr);
        sessionManager.broadcast(gameID, username, notifyMsg);
    }

    public void parseAsMakeMove(Session session, String message) throws DataAccessException {
        MakeMoveGameCommand gameCommand = ChessSerializer.gson().fromJson(message, MakeMoveGameCommand.class);
        System.out.printf("MAKE_MOVE | gameID: %d, move: %d.%d to %d.%d%n", gameCommand.getGameID(),
                gameCommand.getMove().getStartPosition().getRow(), gameCommand.getMove().getStartPosition().getColumn(),
                gameCommand.getMove().getEndPosition().getRow(), gameCommand.getMove().getEndPosition().getColumn());

        requireValidAuthString(gameCommand);
        requireUnfinishedGame(gameCommand.getGameID());
        ChessGame.TeamColor playerColor = requireColor(gameCommand.getAuthString(), gameCommand.getGameID());

        Game game = gameDAO.findGame(gameCommand.getGameID());
        ChessGame chessGame = game.chessGame();
        if (!playerColor.equals(chessGame.getTeamTurn())) {
            wsServer.sendError(session, "It's the other player's turn right now.");
            return;
        }

        ChessMove move = gameCommand.getMove();
        try {
            chessGame.makeMove(move);
            gameDAO.updateGameState(game);
        } catch (InvalidMoveException e) {
            wsServer.sendError(session, e, "Invalid move.");
            return;
        }

        LoadGameServerMessage loadMessage = new LoadGameServerMessage(game);
        sessionManager.broadcastAll(gameCommand.getGameID(), loadMessage);

        String username = authDAO.getUsername(gameCommand.getAuthString());
        String msg = getMoveNotificationString(game, username, move);
        NotificationServerMessage notifyMessage = new NotificationServerMessage(msg);
        sessionManager.broadcast(gameCommand.getGameID(), username, notifyMessage);
    }

    private String getMoveNotificationString(Game game, String username, ChessMove move) {
        String moveString = String.format("User %s has made move %s", username, move.toString());
        StringBuilder builder = new StringBuilder(moveString);
        appendGameInfo(builder, game);
        return builder.toString();
    }

    private void appendGameInfo(StringBuilder builder, Game game) {
        ChessGame chessGame = game.chessGame();
        String whiteUser = game.whiteUsername();
        String blackUser = game.blackUsername();


        if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
            builder.append(String.format(" White (%s) is in check.", whiteUser));
        } else if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
            builder.append(String.format(" Black (%s) is in check.", blackUser));
        }

        if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            builder.append(String.format(" White (%s) is in checkmate.", whiteUser));
        } else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            builder.append(String.format(" Black (%s) is in checkmate.", blackUser));
        }

        if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
            builder.append(String.format(" White (%s) is in stalemate.", whiteUser));
        } else if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            builder.append(String.format(" Black (%s) is in stalemate.", blackUser));
        }

        if (chessGame.getWinState() == WinState.WHITE_WIN) {
            builder.append("\nGame over: White has won!");
        } else if (chessGame.getWinState() == WinState.BLACK_WIN) {
            builder.append("\nGame over: Black has won!");
        }
    }

    private ChessGame.TeamColor requireColor(String authString, int gameID) throws DataAccessException {
        String username = authDAO.getUsername(authString);
        Game game = gameDAO.findGame(gameID);

        ChessGame.TeamColor playerColor;
        if (Objects.equals(username, game.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(username, game.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new BadRequestException("Called requireColor() when user '" + username + "' was an observer");
        }
        return playerColor;
    }

    private void requireUnfinishedGame(int gameID) throws DataAccessException {
        Game game = gameDAO.findGame(gameID);
        ChessGame chessGame = game.chessGame();
        WinState winState = chessGame.getWinState();
        if (winState != WinState.IN_PROGRESS) {
            String msg = String.format("Called requireUnfinishedGame() when winState of game %d was %s", gameID,
                    winState.name());
            throw new BadRequestException(msg);
        }
    }

    public void parseAsLeave(Session session, String message) throws DataAccessException {
        LeaveGameCommand gameCommand = ChessSerializer.gson().fromJson(message, LeaveGameCommand.class);
        System.out.printf("LEAVE | gameID: %d%n", gameCommand.getGameID());

        requireValidAuthString(gameCommand);
        requireHasRoleInGame(gameCommand.getAuthString(), gameCommand.getGameID());

        String username = authDAO.getUsername(gameCommand.getAuthString());
        String msg = String.format("User %s left the game", username);
        sessionManager.removeUser(gameCommand.getGameID(), username);
        sessionManager.broadcast(gameCommand.getGameID(), username, new NotificationServerMessage(msg));
    }

    public void parseAsResign(Session session, String message) throws DataAccessException {
        ResignGameCommand gameCommand = ChessSerializer.gson().fromJson(message, ResignGameCommand.class);
        System.out.printf("RESIGN | gameID: %d%n", gameCommand.getGameID());

        requireValidAuthString(gameCommand);
        requireUnfinishedGame(gameCommand.getGameID());

        ChessGame.TeamColor playerColor = requireColor(gameCommand.getAuthString(), gameCommand.getGameID());
        if (playerColor == null) {
            wsServer.sendError(session, "Only active players can resign.");
            return;
        }

        Game game = gameDAO.findGame(gameCommand.getGameID());
        ChessGame chessGame = game.chessGame();
        chessGame.resign(playerColor);
        gameDAO.updateGameState(game);

        String username = authDAO.getUsername(gameCommand.getAuthString());
        String msg = String.format("User %s (%s) has resigned the game.", username, playerColor.name());
        NotificationServerMessage serverMessage = new NotificationServerMessage(msg);
        sessionManager.broadcastAll(gameCommand.getGameID(), serverMessage);
    }
}
