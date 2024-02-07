package webSocketMessages.userCommands;

public class ResignGameCommand extends UserGameCommand {
    private final int gameID;

    public ResignGameCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameID() {
        return gameID;
    }
}