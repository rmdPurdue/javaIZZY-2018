package Exceptions;

public class EStopException extends Exception {
    public EStopException(final String message) {
        super(message);
    }

    public EStopException(final String message, final Exception e) {
        super(message, e);
    }
}
