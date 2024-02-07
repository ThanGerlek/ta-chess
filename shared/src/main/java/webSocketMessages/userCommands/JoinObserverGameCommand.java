package webSocketMessages.userCommands;

public class JoinObserverGameCommand extends UserGameCommand {
    private final int gameID;

    public JoinObserverGameCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public int getGameID() {
        return gameID;
    }
}