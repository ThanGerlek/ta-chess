package server.webSocket;

import dataAccess.*;
import http.ChessSerializer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorServerMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketServer {
    private final UserGameCommandHandler cmdHandler;

    public WebSocketServer(AuthDAO authDAO, GameDAO gameDAO) {
        this.cmdHandler = new UserGameCommandHandler(authDAO, gameDAO, this);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand gameCommand = ChessSerializer.gson().fromJson(message, UserGameCommand.class);
            switch (gameCommand.getCommandType()) {
                case JOIN_PLAYER -> cmdHandler.parseAsJoinPlayer(session, message);
                case JOIN_OBSERVER -> cmdHandler.parseAsJoinObserver(session, message);
                case MAKE_MOVE -> cmdHandler.parseAsMakeMove(session, message);
                case LEAVE -> cmdHandler.parseAsLeave(session, message);
                case RESIGN -> cmdHandler.parseAsResign(session, message);
            }
        } catch (UnauthorizedAccessException e) {
            sendError(session, e, "Invalid token. Are you logged in correctly?");
        } catch (BadRequestException e) {
            sendError(session, e, "Sorry, you can't do that right now.");
        } catch (NoSuchItemException e) {
            sendError(session, e, "Not found. Did you enter everything correctly?");
        } catch (Exception e) {
            sendError(session, e, "Sorry, there's an unknown problem. Please try again.");
            System.out.println("Caught generic throwable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendError(Session session, Throwable e, String errMsg) {
        System.err.println("Server sent an error while parsing UserGameCommand: '" + errMsg + "'\n\tOriginal error: " +
                e.getMessage());
        send(session, new ErrorServerMessage(errMsg));
    }

    public void send(Session session, ServerMessage serverMessage) {
        String messageJson = ChessSerializer.gson().toJson(serverMessage);
        try {
            System.out.println("Sending to session " + session.hashCode() + " with message " + messageJson);
            session.getRemote().sendString(messageJson);
        } catch (IOException e) {
            System.err.println("Failed to send WebSocket message with error: " + e.getMessage());
        }
    }

    @OnWebSocketError
    public void onWebSocketError(Session session, Throwable exception) {
        System.err.println("Server threw uncaught WebSocket error: " + exception.getMessage());
    }

    public void sendError(Session session, String errMsg) {
        System.err.println("Server sent an error while parsing UserGameCommand: " + errMsg);
        send(session, new ErrorServerMessage(errMsg));
    }

}