package LineFollowing;

import com.pi4j.io.gpio.GpioPinAnalogInput;

public class LineSensor {
    private int threshold;  // the analog value at which a sensor is considered to be reading a wire
                            // (ranges for analog feedback are roughly 3000 if reading - 18000 if not reading)
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
        return this.analogInput.getValue();
    }

    /**
     * Returns the state of the line sensor (reading the wire or not reading the wire)
     *
     * @return Boolean representing if the sensor is detecting a wire
     */
    public boolean getSensorState() {
        return (getSensorReading() < getThreshold()); // is sensor reading (lower value = reading)
    }

    public String getName() {
        return analogInput.getName();
    }

}

