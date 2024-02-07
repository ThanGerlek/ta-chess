package websocket;

@FunctionalInterface
public interface NotificationHandler {
    void notify(String message);
}
