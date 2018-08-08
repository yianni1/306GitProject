package exceptions;

/**
 * Thrown if a task that is not scheduleable is attempted to be scheduled.
 */
public class NotSchedulableException extends RuntimeException{

    public NotSchedulableException() {super(); }

    public NotSchedulableException(String message) {
        super(message);
    }

}
