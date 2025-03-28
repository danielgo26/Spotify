package exception;

public class SaveDataException extends Exception {

    public SaveDataException(String message) {
        super(message);
    }

    public SaveDataException(String message, Throwable cause) {
        super(message, cause);
    }

}