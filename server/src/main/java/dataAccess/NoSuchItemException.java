package dataAccess;

public class NoSuchItemException extends DataAccessException {
    public NoSuchItemException(String message) {
        super(message);
    }
}
