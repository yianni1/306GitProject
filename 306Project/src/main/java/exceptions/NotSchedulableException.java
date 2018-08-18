package exceptions;

/**
 * Thrown if a task that is not scheduleable is attempted to be scheduled.
 */
public class NotSchedulableException extends RuntimeException{

    /**
     * Creates a NotSchedulableException with no message with RuntimeException
     */
    public NotSchedulableException() {super(); }

    /**
     * Creates a NotSchedulableException with a message
     * @param message when the exception is thrown
     */
    public NotSchedulableException(String message) {
        super(message);
    }

}
