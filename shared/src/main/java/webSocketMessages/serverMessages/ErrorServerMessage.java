package webSocketMessages.serverMessages;

public class ErrorServerMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorServerMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "[Error] " + errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}