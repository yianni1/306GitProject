package exceptions;

/**
 * This exception is thrown if the task is trying to be descheduled but cannot be descheduled since the children haven't
 * been able to be descheduled yet.
 */
public class NotDeschedulableException extends RuntimeException{

    /**
     * This exception takes the attributes of its parent the RunTime exception
     */
    public NotDeschedulableException() {super(); }

    /**
     * The deschedulable exception with an output message which is given when the exception is caused
     * @param message when the exception is caused
     */
    public NotDeschedulableException(String message) {
        super(message);
    }

}
