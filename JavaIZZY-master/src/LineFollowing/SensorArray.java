package LineFollowing;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.Iterator;

public class SensorArray
{
    private final int NUMBER_OF_SENSORS = 3;

    private int error; //the stored value for the current error for the PID loop P function (in mm)
    private int errorSum; //the combined value of all previous errors for the PID loop I function (in mm)
    private int previousError; //the stored value of the previous error for the PID loop D function (in mm)
    private int kp; //the ratio of the effect of the P value in the PID loop, adjusted for tuning
    private int ki; //the ratio of the effect of the I value in the PID loop, adjusted for tuning
    private int kd; //the ratio of the effect of the D value in the PID loop, adjusted for tuning
    private double yDistance; //the distance from the sensors to the center of the wheels for the error angle calculation (in mm)
    private int pidValue; //the stored PID value for the horizontal error
    private boolean[] signalArray; //the states of all sensors
    private int sensorWidth; //the diameter of one sensor (in mm)
    private int sensorSpacing; //the spacing between sensors (in mm)

    ArrayList<LineSensor> sensorList = new ArrayList<>(); //an ArrayList of all sensors stored in order from left to right

    /**
     * Constructor for SensorArray class
     *
     * @param kp the effect of the P value in the PID loop
     * @param ki the effect of the I value in the PID loop
     * @param kd the effect of the D value in the PID loop
     * @param yDistance the distance from the center sensor to the system center (between both wheels) (in mm)
     */
    public SensorArray(int kp, int ki, int kd, double yDistance, int sensorWidth, int sensorSpacing) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.yDistance = yDistance;
        this.errorSum = 0;
        this.error = 0;
        this.signalArray = new boolean[NUMBER_OF_SENSORS];
        this.sensorWidth = sensorWidth;
        this.sensorSpacing = sensorSpacing;
    }

    /**
     * Adds a LineSensor object to the SensorArray. Should be added in sequential order
     *
     * @param sensor a line sensor object
     */
    public void addSensor(LineSensor sensor){
        sensorList.add(sensor);
    }

    /**
     * Returns the kP value
     *
     * @return kP value of the PID loop
     */
    public int getKp() {
        return this.kp;
    }

    /**
     * Sets the kP value
     *
     * @param kp kP value of the PID loop
     */
    public void setKp(int kp) {
        this.kp = kp;
    }

    /**
     * Returns the kI value
     *
     * @return kI value of the PID loop
     */
    public int getKi() {
        return this.ki;
    }

    /**
     * Sets the kI value
     *
     * @param ki kI value of the PID loop
     */
    public void setKi(int ki) {
        this.ki = ki;
    }

    /**
     * Returns kD value
     *
     * @return kD value of PID loop
     */
    public int getKd() {
        return this.kd;
    }

    /**
     * Sets the kD value
     *
     * @param kd kD value of the PID loop
     */
    public void setKd(int kd) {
        this.kd = kd;
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
    public void setYDistance(double yDistance) {
        this.yDistance = yDistance;
    }

    /**
     * Calculates the adjustment angle based off of the current PID value (horizontal error)
     * and the yDistance of the system
     *
     * @return the angle the system needs to turn in order to stay on the wire path
     */
    public double getErrorAngle() {
        return -(Math.atan(getPidValue() / getYDistance()));
    }

    /**
     * Sets the sensorState array based on the values of all LineSensors in the SensorArray.
     * Currently only setup for 3 sensors //TODO: make dynamic to the amount of sensors
     *
     * Throws a rideStop exception if all three sensors are reading the same value (+ or -)
     *
     * Throws an eStop exception if there is a catastrophic failure
     */
    public void readSensors() throws RideStopException, EStopException{
            for (int i = 0; i < sensorList.size(); i++) {
                //System.out.println(sensorList.get(i).getAnalogInput().getName() + " = " + sensorList.get(i).getSensorReading());
                signalArray[i] = sensorList.get(i).getSensorState(); //checks if wire is under sensor
            }
            if (signalArray[0] && signalArray[1] && signalArray[2]) {           //  1   1   1
                throw new RideStopException("All sensor positive");
            } else if (!signalArray[0] && !signalArray[1] && !signalArray[2]) {   //  -   -   -
                throw new RideStopException("All sensor negative");
            } else if (signalArray[0] && !signalArray[1] && !signalArray[2]) {    //  1   -   -
                error = -(sensorSpacing + sensorWidth);
            } else if (signalArray[0] && signalArray[1] && !signalArray[2]) {     //  1   1   -
                error = -(sensorSpacing);
            } else if (!signalArray[0] && signalArray[1] && !signalArray[2]) {    //  -   1   -
                error = 0;
            } else if (!signalArray[0] && signalArray[1] && signalArray[2]) {     //  -   1   1
                error = sensorSpacing;
            } else if (!signalArray[0] && !signalArray[1] && signalArray[2]) {    //  -   -   1
                error = sensorSpacing + sensorWidth;
            } else {
                throw new EStopException("Critical error in sensor states");
            }
    }

    /**
     * Calculates the PID value (horizontal error) based on sensor error values
     */
    public void calculatePID() {
        int proportional = getKp() * error;
        int integral = getKi() * errorSum;
        int derivative = getKd() * previousError;
        pidValue = proportional + integral + derivative;
        if (pidValue > 180) {
            pidValue = 180;
        }
        errorSum += error;
        previousError = error;
    }

    /**
     * Returns PID value after calculating (call readSensors and calculatePID first)
     *
     * @return PID value (horizontal error)
     */
    public double getPidValue() {
        return pidValue;
    }
}
