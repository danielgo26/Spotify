package exception.runtime;

public class ServerStartUpException extends RuntimeException {

    public ServerStartUpException(String message) {
        super(message);
    }

    public ServerStartUpException(String message, Throwable cause) {
        super(message, cause);
    }

}
