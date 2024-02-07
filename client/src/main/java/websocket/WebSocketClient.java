package websocket;

import httpConnection.FailedConnectionException;
import http.ChessSerializer;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint {
    private final String serverURL;
    private Session session;
    private NotificationHandler notificationHandler;

    public WebSocketClient(String serverURL) {
        this.session = null;
        this.notificationHandler = null;
        this.serverURL = serverURL;
    }

    public void openConnection(NotificationHandler notificationHandler) throws FailedConnectionException {
        this.notificationHandler = notificationHandler;
        // TODO close previous connection?
        try {
            URI socketURI = getURI(serverURL);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            addMessageHandler();
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new FailedConnectionException("Failed to create WebSocket connection: " + e.getMessage());
        }
    }

    private URI getURI(String serverURL) throws URISyntaxException {
        String uriString = serverURL.replace("https", "ws").replace("http", "ws") + "/connect";
        return new URI(uriString);
    }

    private void addMessageHandler() {
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                notificationHandler.notify(s);
            }
        });
    }

    public void closeConnection() throws FailedConnectionException {
        try {
            this.session.close();
        } catch (IOException e) {
            throw new FailedConnectionException("Threw an error while closing WebSocket connection: " + e.getMessage());
        }
    }

    public void send(UserGameCommand gameCommand) throws FailedConnectionException {
        String messageJson = ChessSerializer.gson().toJson(gameCommand);
        try {
            session.getBasicRemote().sendText(messageJson);
        } catch (IOException e) {
            throw new FailedConnectionException(
                    "Client failed to send WebSocket message with error: " + e.getMessage());
        } catch (IllegalStateException e) {
            // Connection may have closed; try again
            openConnection(notificationHandler);
            try {
                session.getBasicRemote().sendText(messageJson);
            } catch (IOException e2) {
                throw new FailedConnectionException(
                        "Client failed to send WebSocket message with IllegalStateException, then with error: " +
                                e2.getMessage() + "\n\tOriginal error: " + e.getMessage());
            }
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
