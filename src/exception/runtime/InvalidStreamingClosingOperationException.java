package exception.runtime;

public class InvalidStreamingClosingOperationException extends RuntimeException {

    public InvalidStreamingClosingOperationException(String message) {
        super(message);
    }

    public InvalidStreamingClosingOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}