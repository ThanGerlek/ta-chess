package client;

import httpConnection.ChessServerFacade;
import httpConnection.FailedConnectionException;
import httpConnection.FailedResponseException;
import ui.BoardDrawer;
import ui.ConsoleUI;
import ui.command.Command;
import ui.command.Commands;
import ui.command.UICommand;
import websocket.NotificationHandler;
import websocket.WebSocketClient;
import http.AuthResponse;
import http.GameListItem;
import model.Game;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final NotificationHandler notificationHandler;
    private final ConsoleUI ui;
    private final ChessServerFacade serverFacade;
    private final WebSocketClient ws;
    private final SessionData sessionData;
    private Game game;

    public ChessClient(String serverURL, ConsoleUI ui, NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
        this.ui = ui;
        this.serverFacade = new ChessServerFacade(serverURL);
        this.ws = new WebSocketClient(serverURL);
        this.sessionData = new SessionData();
    }

    public SessionData getSessionData() {
        return sessionData;
    }

    public void rejectAuthorization() {
        ui.println("Woah! You're not allowed to do that right now. Try logging in first.");
    }

    public void setCurrentGame(Game game) {
        this.game = game;
    }

    public void printHelpMenu() {
        ui.println("Available commands:");
        for (UICommand cmd : Commands.UI_COMMANDS) {
            if (isAuthorizedToRun(cmd)) {
                ui.println("\t" + getHelpStringForCommand(cmd));
            }
        }
    }

    public boolean isAuthorizedToRun(Command cmd) {
        return cmd.canBeRunBy(sessionData.getAuthRole());
    }

    private String getHelpStringForCommand(UICommand cmd) {
        return String.format("%s - %s", SET_TEXT_BOLD + cmd.getCommandString() + RESET_TEXT_BOLD_FAINT,
                SET_TEXT_ITALIC + cmd.getDescription() + RESET_TEXT_ITALIC);
    }

    public void quit() {
        ui.println("Goodbye!");
    }

    public void register() throws FailedConnectionException, FailedResponseException {
        ui.println("Please enter a username and password.");
        ui.println(String.format(
                "%sWARNING: DO NOT USE A REAL PASSWORD.%s This program was built by a college undergrad, not a " +
                        "security" + " professional. It is NOT secure.", SET_TEXT_BOLD, RESET_TEXT_BOLD_FAINT));
        String username = ui.promptInput("Username: ");
        String password = ui.promptInput("Password: ");
        String email = ui.promptInput("Email (optional): ");

        AuthResponse response = serverFacade.register(username, password, email);
        sessionData.setUserData(response.authToken(), username);
        sessionData.setAuthRole(AuthorizationRole.GUEST);
    }

    public void login() throws FailedConnectionException, FailedResponseException {
        String username = ui.promptInput("Username: ");
        String password = ui.promptInput("Password: ");

        AuthResponse response = serverFacade.login(username, password);
        sessionData.setUserData(response.authToken(), username);
        sessionData.setAuthRole(AuthorizationRole.USER);
    }

    public void logout() throws FailedConnectionException, FailedResponseException {
        serverFacade.logout(sessionData.getAuthTokenString());
        sessionData.clearUserData();
    }

    public void createGame() throws FailedConnectionException, FailedResponseException {
        String gameName = ui.promptInput("Enter a name for this game: ");
        serverFacade.createGame(gameName, sessionData.getAuthTokenString());
    }

    public void joinGame() throws FailedConnectionException, FailedResponseException {
        joinGame(false);
    }

    private void joinGame(boolean asSpectator) throws FailedConnectionException, FailedResponseException {
        ArrayList<GameListItem> games = listGames();
        if (games != null) {
            try {
                GameJoiner joiner = new GameJoiner(ui, serverFacade, sessionData, games);
                joiner.joinGame(asSpectator);
            } catch (CommandCancelException e) {
                return;
            }

            ws.openConnection(notificationHandler);

            String authToken = sessionData.getAuthTokenString();
            int gameID = sessionData.getGameID();
            UserGameCommand gameCommand;
            if (asSpectator) {
                gameCommand = new JoinObserverGameCommand(authToken, gameID);
            } else {
                gameCommand = new JoinPlayerGameCommand(authToken, gameID, sessionData.getPlayerColor());
            }

            ws.send(gameCommand);
        }
    }

    public ArrayList<GameListItem> listGames() throws FailedConnectionException, FailedResponseException {
        ArrayList<GameListItem> games = serverFacade.listGames(sessionData.getAuthTokenString());
        printGameList(games);
        return games;
    }

    private void printGameList(ArrayList<GameListItem> games) {
        if (games.isEmpty()) {
            ui.println("There are no currently active games. Use the 'create' command to add one.");
        } else {
            ui.println(String.format("Currently active games: %d", games.size()));
            for (int i = 0; i < games.size(); i++) {
                ui.println(formatGameInfoString(i, games.get(i)));
            }
        }
    }

    private String formatGameInfoString(int gameNumber, GameListItem game) {
        String whitePlayer = formatUsernameOutput(game.whiteUsername());
        String blackPlayer = formatUsernameOutput(game.blackUsername());
        return String.format("\t[%d] Game name: '%s', white player: %s, black player: %s", gameNumber, game.gameName(),
                whitePlayer, blackPlayer);
    }

    private String formatUsernameOutput(String username) {
        return (username == null || username.isEmpty()) ? "None" : "'" + username + "'";
    }

    public void observeGame() throws FailedConnectionException, FailedResponseException {
        joinGame(true);
    }

    public void drawBoard() {
        BoardDrawer drawer = new BoardDrawer(ui, game.chessGame().getBoard());
        drawer.setViewerTeamColor(sessionData.getPlayerColor());
        drawer.draw();
    }

    public void leaveGame() throws FailedConnectionException {
        ws.send(new LeaveGameCommand(sessionData.getAuthTokenString(), sessionData.getGameID()));
        // TODO Race condition?
//        ws.closeConnection();
        sessionData.clearGameData();
        sessionData.setAuthRole(AuthorizationRole.USER);
    }

    public void makeMove() throws FailedConnectionException {
        try {
            MoveMaker moveMaker = new MoveMaker(ui, ws, sessionData, game);
            moveMaker.makeMove();
        } catch (CommandCancelException ignored) {
            return;
        }
    }

    public void resign() throws FailedConnectionException {
        String input = ui.promptInput(
                "Are you sure you want to resign? This cannot be undone. Enter 'confirm' if so, or anything else to " +
                        "cancel: ");
        if ("confirm".equals(input)) {
            ws.send(new ResignGameCommand(sessionData.getAuthTokenString(), sessionData.getGameID()));
            sessionData.setAuthRole(AuthorizationRole.OBSERVER);
        }
    }

    public void highlightMoves() {
        // TODO
    }
}
