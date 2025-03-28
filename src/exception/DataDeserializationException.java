package exception;

public class DataDeserializationException extends Exception {

    public DataDeserializationException(String message) {
        super(message);
    }

    public DataDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
