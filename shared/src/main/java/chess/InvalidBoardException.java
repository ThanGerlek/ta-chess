package chess;

/**
 * Indicates an undefined call was made on an invalid board state
 */
public class InvalidBoardException extends RuntimeException {
    public InvalidBoardException(String message) {
        super(message);
    }
}
