package exception.runtime;

public class InvalidChannelClosingOperationException extends RuntimeException {

    public InvalidChannelClosingOperationException(String message) {
        super(message);
    }

    public InvalidChannelClosingOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}