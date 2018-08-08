package exceptions;

/**
 * Thrown if a task that cannot be descheduled is attempted to be descheduled.
 */
public class NotDeschedulableException extends RuntimeException{

    public NotDeschedulableException() {super(); }

    public NotDeschedulableException(String message) {
        super(message);
    }

}
