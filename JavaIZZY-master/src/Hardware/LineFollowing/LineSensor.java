package Hardware.LineFollowing;

import com.pi4j.io.gpio.GpioPinAnalogInput;

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
    private final GpioPinAnalogInput analogInput; // the GPIO Pin that the sensor is located at

    /**
     * Constructor for a LineSensor Object
     *
     * @param threshold the analog value at which a sensor is considered to be reading a wire
     * @param analogInput the GPIO Pin that the sensor is located at
     */
    public LineSensor(final int threshold, final GpioPinAnalogInput analogInput) {
        this.threshold = threshold;
        this.analogInput = analogInput;
        this.maxReading = 6000;
        this.minReading = 17000;
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
    public double getSensorReading() {
        reading = this.analogInput.getValue();
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
        return analogInput.getName();
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

