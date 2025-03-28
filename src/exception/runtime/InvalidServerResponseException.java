package exception.runtime;

public class InvalidServerResponseException extends RuntimeException {

    public InvalidServerResponseException(String message) {
        super(message);
    }

    public InvalidServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}