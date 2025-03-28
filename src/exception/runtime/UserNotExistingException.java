package exception.runtime;

public class UserNotExistingException extends RuntimeException {

    public UserNotExistingException(String message) {
        super(message);
    }

    public UserNotExistingException(String message, Throwable cause) {
        super(message, cause);
    }

}
