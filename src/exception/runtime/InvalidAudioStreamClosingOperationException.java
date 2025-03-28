package exception.runtime;

public class InvalidAudioStreamClosingOperationException extends RuntimeException {

    public InvalidAudioStreamClosingOperationException(String message) {
        super(message);
    }

    public InvalidAudioStreamClosingOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}