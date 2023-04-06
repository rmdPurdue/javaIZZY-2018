package com.rmdPurdue.izzyRobot.hardware.ad1115;

public class ContinuousMeasuringException extends RuntimeException{

    public ContinuousMeasuringException(String message) {
        super(message);
    }

    public ContinuousMeasuringException(String message, Throwable cause) {
        super(message, cause);
    }
}