package LineFollowing;

import java.util.ArrayList;

public class SensorArray
{
    private final int NUMBER_OF_SENSORS = 3;

    private double yDistance; //the distance from the sensors to the center of the wheels for the error angle calculation (in mm)
    private boolean[] signalArray; //the states of all sensors
    private int sensorWidth; //the diameter of the sensor (in mm)
    private int sensorSpacing; //the spacing between sensors (in mm)
    ArrayList<LineSensor> sensorList = new ArrayList<>(); //an ArrayList of all sensors stored in order from left to right

    public SensorArray(final double yDistance, final int sensorSpacing, final int sensorWidth) {
        this.yDistance = yDistance;
        this.signalArray = new boolean[NUMBER_OF_SENSORS];
        this.sensorSpacing = sensorSpacing;
        this.sensorWidth = sensorWidth;
    }

    /**
     * Adds a LineSensor object to the SensorArray. Should be added in sequential order (L/C/R)
     *
     * @param sensor a line sensor object
     */
    public void addSensor(final LineSensor sensor){
        sensorList.add(sensor);
    }

    /**
     * Returns the yDistance of the system in mm
     *
     * @return y Distance (in mm) of system measured from center sensor to middle of wheels
     */
    public double getYDistance() {
        return this.yDistance;
    }

    /**
     * Sets the yDistance of the system in mm
     *
     * @param yDistance y Distance (in mm) of system measured from center sensor to middle of wheels
     */
    public void setYDistance(final double yDistance) {
        this.yDistance = yDistance;
    }

    /**
     * Returns the diameter of the sensor. This value should be the distance that the sensor detects the line
     * without any other sensor sensing the line (in mm)
     *
     * @return sensor width
     */
    public int getSensorWidth() {
        return sensorWidth;
    }

    /**
     * Returns the width of the sensor reading area (in mm)
     *
     * @param sensorWidth
     */
    public void setSensorWidth(final int sensorWidth) {
        this.sensorWidth = sensorWidth;
    }

    /**
     * Returns the distance between two sensors (edge to edge). This value should be the distance that two sensors
     * detect the line (in mm)
     *
     * @return sensor spacing
     */
    public int getSensorSpacing() {
        return sensorSpacing;
    }

    /**
     * Sets distance where two sensors can detect the line at the same time. (in mm)
     *
     * @param sensorSpacing distance of overlapping sensor detection areas
     */
    public void setSensorSpacing(final int sensorSpacing) {
        this.sensorSpacing = sensorSpacing;
    }

    /**
     * Sets the sensorState array based on the values of all LineSensors in the SensorArray.
     * Currently only setup for 3 sensors //TODO: make dynamic to the amount of sensors
     */
    public boolean[] readSensors() {
        for (int i = 0; i < sensorList.size(); i++) {
            //System.out.println(sensorList.get(i).getAnalogInput().getName() + " = " + sensorList.get(i).getSensorReading());
            signalArray[i] = sensorList.get(i).getSensorState(); //checks if wire is under sensor
        }
        return signalArray;
    }

}
