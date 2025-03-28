package exception;

public class FileNotCreatedException extends Exception {

    public FileNotCreatedException(String message) {
        super(message);
    }

    public FileNotCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
