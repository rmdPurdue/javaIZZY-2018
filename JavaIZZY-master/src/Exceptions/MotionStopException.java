package Exceptions;

public class MotionStopException extends Exception {
    public MotionStopException(final String message) {
        super(message);
    }

    public MotionStopException(final String message, final Exception e) {
        super(message, e);
    }
}
