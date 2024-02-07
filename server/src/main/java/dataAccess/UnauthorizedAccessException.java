package dataAccess;

public class UnauthorizedAccessException extends DataAccessException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
