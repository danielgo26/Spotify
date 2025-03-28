package exception;

public class InvalidLogOperationException extends Exception {

    public InvalidLogOperationException(String message) {
        super(message);
    }

    public InvalidLogOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
