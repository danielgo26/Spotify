package exception.runtime;

public class UnsupportedAudioFormatException extends RuntimeException {

    public UnsupportedAudioFormatException(String message) {
        super(message);
    }

    public UnsupportedAudioFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}