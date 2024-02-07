package webSocketMessages.serverMessages;

import model.Game;

public class LoadGameServerMessage extends ServerMessage {
    private final Game game;

    public LoadGameServerMessage(Game game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}