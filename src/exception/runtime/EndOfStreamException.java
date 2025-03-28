package exception.runtime;

public class EndOfStreamException extends RuntimeException {

    public EndOfStreamException(String message) {
        super(message);
    }

    public EndOfStreamException(String message, Throwable cause) {
        super(message, cause);
    }

}