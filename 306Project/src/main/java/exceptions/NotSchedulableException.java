package exceptions;

public class NotSchedulableException extends RuntimeException{

    public NotSchedulableException() {super(); }

    public NotSchedulableException(String message) {
        super(message);
    }

}
