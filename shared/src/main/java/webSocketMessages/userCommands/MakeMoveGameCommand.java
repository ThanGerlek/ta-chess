package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveGameCommand extends UserGameCommand {
    private final int gameID;
    private final ChessMove move;

    public MakeMoveGameCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.MAKE_MOVE;
        this.move = move;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}