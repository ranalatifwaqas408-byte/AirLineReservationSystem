package exceptions;

public class InsufficientSeatsException extends Exception {
    public InsufficientSeatsException(String message) {
        super(message);
    }
}
