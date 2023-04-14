package com.rmdPurdue.izzyRobot.hardware.lineFollowing;

import com.rmdPurdue.izzyRobot.exceptions.EStopException;

import java.util.ArrayList;

/**
 * Holds all sensors that are used to determine line position
 */
public class SensorArray
{
    private final int NUMBER_OF_SENSORS = 2;

    private double yDistance; //the distance from the sensors to the center of the wheels for the error angle calculation (in mm)
    private boolean[] signalArray; //the states of all sensors
    private int[] analogArray; //the analog values of all sensors
    private int[] thresholdArray; //the thresholds of all sensors
    private int sensorWidth; //the diameter of the sensor (in mm)
    private int sensorSpacing; //the spacing between sensors (in mm)
    ArrayList<LineSensor> sensorList = new ArrayList<>(); //an ArrayList of all sensors stored in order from left to right

    public SensorArray(final double yDistance, final int sensorSpacing, final int sensorWidth) {
        this.yDistance = yDistance;
        this.signalArray = new boolean[NUMBER_OF_SENSORS];
        this.analogArray = new int[NUMBER_OF_SENSORS];
        this.thresholdArray = new int[NUMBER_OF_SENSORS];
        this.sensorSpacing = sensorSpacing;
        this.sensorWidth = sensorWidth;
    }

    /**
     * Adds a LineSensor object to the SensorArray. Should be added in sequential order (L/C/R) or (L/R)
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
     */
    public int[] readSensorsAnalog() throws EStopException {
        for (int i = 0; i < sensorList.size(); i++) {
//            System.out.println(sensorList.get(i).getName() + " = " + sensorList.get(i).getSensorReading());
            analogArray[i] = (int) sensorList.get(i).getSensorReading();
            signalArray[i] = sensorList.get(i).getSensorState(); //checks if wire is under sensor
        }
        return analogArray;
    }

    /**
     * Sets the sensorState array based on the values of all LineSensors in the SensorArray.
     */
    public boolean[] readSensorsBoolean() throws EStopException {
        for (int i = 0; i < sensorList.size(); i++) {
//            System.out.println(sensorList.get(i).getName() + " = " + sensorList.get(i).getSensorReading());
            analogArray[i] = (int) sensorList.get(i).getSensorReading();
            signalArray[i] = sensorList.get(i).getSensorState(); //checks if wire is under sensor
        }
        return signalArray;
    }

    /**
     * Gets analog values of all sensors from last reading
     * @return integer array of analog sensor values (L, C, R) or (L, R)
     */
    public int[] getSensorsAnalog() {
        return analogArray;
    }

    /**
     * Reads all sensor thresholds and returns an array representing each sensor's integer threshold.
     */
    public int[] getSensorThresholds() {
        for (int i = 0; i < sensorList.size(); i++) {
            thresholdArray[i] = sensorList.get(i).getThreshold(); //returns threshold
        }
        return thresholdArray;
    }

    /**
     *
     * @param leftThreshold analog value of threshold for left sensor
     * @param centerThreshold analog value of threshold for center sensor
     * @param rightThreshold analog value of threshold for right sensor
     */
    public void setSensorThresholds(int leftThreshold, int centerThreshold, int rightThreshold) {
        if (sensorList.size() != 2) {
            throw new RuntimeException("Sensor Array Size != Threshold Size");
        }
        sensorList.get(0).setThreshold(leftThreshold);
        sensorList.get(1).setThreshold(rightThreshold);
    }

    /**
     *
     * @param leftThreshold analog value of threshold for left sensor
     * @param rightThreshold analog value of threshold for right sensor
     */
    public void setSensorThresholds(int leftThreshold, int rightThreshold) {
        if (sensorList.size() != 2) {
            throw new RuntimeException("Sensor Array Size != Threshold Size");
        }
        sensorList.get(0).setThreshold(leftThreshold);
        sensorList.get(1).setThreshold(rightThreshold);
    }

    public void setSensorRanges(int sensor0Min, int sensor0Max, int sensor1Min, int sensor1Max) {
        if (sensorList.size() != 2) {
            throw new RuntimeException("Sensor Array Size != Range Size");
        }
        sensorList.get(0).setMinReading(sensor0Min);
        sensorList.get(0).setMaxReading(sensor0Max);
        sensorList.get(1).setMinReading(sensor1Min);
        sensorList.get(1).setMaxReading(sensor1Max);
        sensorList.get(0).updateSlope();
        sensorList.get(1).updateSlope();
    }

    /**
     * Left = negative, Right = positive (horizontal error distance)
     */
    public double calculateDistance() throws EStopException {
        double error = 0.0;
        error -= sensorList.get(0).getSlope() * sensorList.get(0).getSensorReading();
        error += sensorList.get(1).getSlope() * sensorList.get(1).getSensorReading();
        return error;
    }

}
