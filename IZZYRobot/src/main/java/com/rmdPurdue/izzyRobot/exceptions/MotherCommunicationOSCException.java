package com.rmdPurdue.izzyRobot.exceptions;

public class MotherCommunicationOSCException extends MotionStopException {
    public MotherCommunicationOSCException(final String message) {
        super(message);
    }

    public MotherCommunicationOSCException(final String message, final Exception e) {
        super(message, e);
    }
}
