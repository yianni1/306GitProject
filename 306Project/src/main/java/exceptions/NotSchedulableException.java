package exceptions;

/**
 * Thrown if a task that is not scheduleable is attempted to be scheduled.
 */
public class NotSchedulableException extends RuntimeException{

    /**
     * Creates a NotSchedulableException with no message
     */
    public NotSchedulableException() {super(); }

    /**
     * Creates a NotSchedulableException with a mesage
     * @param message
     */
    public NotSchedulableException(String message) {
        super(message);
    }

}
