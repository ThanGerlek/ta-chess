package httpConnection;

public class FailedConnectionException extends Exception {
    public FailedConnectionException(String msg) {
        super(msg);
    }
}
