package exceptions;

/**
 * Created by olive on 7/08/2018.
 */
public class TaskException extends RuntimeException {

    /**
     * Creates a TaskException with a message
     * @param message
     */
    public TaskException(String message) {
        super(message);
    }
}
