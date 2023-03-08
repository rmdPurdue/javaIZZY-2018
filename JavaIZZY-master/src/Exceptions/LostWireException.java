package Exceptions;

public class LostWireException extends RuntimeException {
    public LostWireException() {
        super();
    }

    public LostWireException(String message) {
        super(message);
    }
}
