package com.rmdPurdue.izzyRobot.hardware.lineFollowing;

import com.rmdPurdue.izzyRobot.exceptions.EStopException;
import com.rmdPurdue.izzyRobot.hardware.ad1115.ADS1115;

/**
 * Represents one line sensor hardware object on IZZY
 */
public class LineSensor {
    private final static double CENTER_OF_SENSORS_TO_CENTER_OF_SENSOR = 2.3; // the distance (cm) from the center of izzy to the middle (max reading) of an inductive sensor. accounts for sensor width and spacing
    private int threshold;  // the analog value at which a sensor is considered to be reading a wire
                            // (ranges for analog feedback are roughly 3000 if reading - 18000 if not reading)
    private int maxReading; // reading when wire is directly under sensor
    private int minReading; // reading when wire is not detected at all by sensor
    private double slope; // the slope of reading values
    private double reading;
    private final int ads1115PinNumber; // the GPIO Pin that the sensor is located at
    private final String name;
    private final ADS1115 ads1115; // the GPIO provider

    /**
     * Constructor for a LineSensor Object
     *
     * @param threshold the analog value at which a sensor is considered to be reading a wire
     * @param ads1115PinNumber the Pin Number that the sensor is located at on the ADS1115 (0-3)
     * @param name The label for the sensor
     * @param ads1115 The ADS1115 provider
     */
    public LineSensor(final int threshold, final int ads1115PinNumber, final String name, final ADS1115 ads1115) {
        this.threshold = threshold;
        this.ads1115PinNumber = ads1115PinNumber;
        this.maxReading = 6000;
        this.minReading = 17000;
        this.name = name;
        this.ads1115 = ads1115;
        updateSlope();
    }

    /**
     * Returns the threshold value for when the sensor returns a positive or negative state
     *
     * @return int of the analog value at which a sensor is considered to be reading a wire
     */
    public int getThreshold() {
        return this.threshold;
    }

    /**
     * Adjusts the threshold value for when the sensor returns a positive or negative state.
     *
     * @param threshold is the analog value at which the sensor state should be considered "reading"
     */
    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }

    /**
     * Returns the literal analog value of the sensor, mainly for debugging purposes
     *
     * @return double representing analog value of sensor (roughly 3000 if reading - 18000 if not reading)
     */
    public double getSensorReading() throws EStopException {
        switch (ads1115PinNumber) {
            case 0:
                reading = this.ads1115.getAIn0();
                break;
            case 1:
                reading = this.ads1115.getAIn1();
                break;
            case 2:
                reading = this.ads1115.getAIn2();
                break;
            case 3:
                reading = this.ads1115.getAIn3();
                break;
            default:
                throw new EStopException("Unable to initialize ADS115 - Bad Pin Number");
        }
        return reading;
    }

    /**
     * Returns the state of the line sensor (reading the wire or not reading the wire)
     *
     * @return Boolean representing if the sensor is detecting a wire
     */
    public boolean getSensorState() {
        return (reading < getThreshold()); // is sensor reading (lower value = reading)
    }

    public String getName() {
        return this.name;
    }

    public int getMaxReading() {
        return maxReading;
    }

    public void setMaxReading(int maxReading) {
        this.maxReading = maxReading;
    }

    public int getMinReading() {
        return minReading;
    }

    public void setMinReading(int minReading) {
        this.minReading = minReading;
    }

    public double getSlope() {
        return slope;
    }

    public void updateSlope() {
        this.slope = CENTER_OF_SENSORS_TO_CENTER_OF_SENSOR / (maxReading - minReading);
    }
}

