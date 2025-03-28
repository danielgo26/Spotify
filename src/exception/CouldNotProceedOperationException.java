package exception;

public class CouldNotProceedOperationException extends Exception {

    public CouldNotProceedOperationException(String message) {
        super(message);
    }

    public CouldNotProceedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
