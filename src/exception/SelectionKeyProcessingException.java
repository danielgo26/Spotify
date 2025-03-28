package exception;

public class SelectionKeyProcessingException extends Exception {

    public SelectionKeyProcessingException(String message) {
        super(message);
    }

    public SelectionKeyProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}