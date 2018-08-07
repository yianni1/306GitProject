package exceptions;

public class NotDeschedulableException extends RuntimeException{

    public NotDeschedulableException() {super(); }

    public NotDeschedulableException(String message) {
        super(message);
    }

}
