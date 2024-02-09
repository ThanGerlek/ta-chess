package ui;

public class InvalidUserInputException extends Exception {
    String invalidInputString;
    public InvalidUserInputException(String invalidInputString, String msg) {
        super(msg);
        this.invalidInputString = invalidInputString;
    }

    public String getInvalidInputString() {
        return invalidInputString;
    }
}
