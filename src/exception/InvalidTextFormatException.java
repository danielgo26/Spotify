package exception;

public class InvalidTextFormatException extends Exception {

    public InvalidTextFormatException(String message) {
        super(message);
    }

    public InvalidTextFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}