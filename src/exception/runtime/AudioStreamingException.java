package exception.runtime;

public class AudioStreamingException extends RuntimeException {

    public AudioStreamingException(String message) {
        super(message);
    }

    public AudioStreamingException(String message, Throwable cause) {
        super(message, cause);
    }

}