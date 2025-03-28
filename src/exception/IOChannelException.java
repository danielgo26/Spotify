package exception;

public class IOChannelException extends Exception {

    public IOChannelException(String message) {
        super(message);
    }

    public IOChannelException(String message, Throwable cause) {
        super(message, cause);
    }

}
