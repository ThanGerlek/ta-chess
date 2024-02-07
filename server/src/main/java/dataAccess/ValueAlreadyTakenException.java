package dataAccess;

public class ValueAlreadyTakenException extends DataAccessException {
    public ValueAlreadyTakenException(String message) {
        super(message);
    }
}
