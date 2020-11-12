package LineFollowing;

public class PIDControl {
    private int error; //the stored value for the current error for the PID loop P function (in mm)
    private int errorSum; //the combined value of all previous errors for the PID loop I function (in mm)
    private int previousError; //the stored value of the previous error for the PID loop D function (in mm)
    private int kp; //the ratio of the effect of the P value in the PID loop, adjusted for tuning
    private int ki; //the ratio of the effect of the I value in the PID loop, adjusted for tuning
    private int kd; //the ratio of the effect of the D value in the PID loop, adjusted for tuning
    private int pidValue; //the stored PID value for the horizontal error
    private SensorArray sensorArray;

    public PIDControl(int kp, int ki, int kd, SensorArray sensorArray) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.sensorArray = sensorArray;
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
     * Returns PID value after calculating (call readSensors and calculatePID first)
     *
     * @return PID value (horizontal error)
     */
    public int getPidValue() {
        return pidValue;
    }

    /**
     * Calculates the current error as read by the sensors
     *
     * Throws a motionStop exception if all three sensors are reading the same value (+ or -)
     *
     * Throws an eStop exception if there is a catastrophic failure
     */
    public void adjustError(final boolean[] signalArray) throws MotionStopException, EStopException {
        if (signalArray[0] && signalArray[1] && signalArray[2]) {             //  1   1   1
            throw new MotionStopException("All sensor positive");
        } else if (!signalArray[0] && !signalArray[1] && !signalArray[2]) {   //  -   -   -
            throw new MotionStopException("All sensor negative");
        } else if (signalArray[0] && !signalArray[1] && !signalArray[2]) {    //  1   -   -
            error = -(sensorArray.getSensorWidth() + sensorArray.getSensorSpacing());
        } else if (signalArray[0] && signalArray[1] && !signalArray[2]) {     //  1   1   -
            error = -(sensorArray.getSensorSpacing());
        } else if (!signalArray[0] && signalArray[1] && !signalArray[2]) {    //  -   1   -
            error = 0;
        } else if (!signalArray[0] && signalArray[1] && signalArray[2]) {     //  -   1   1
            error = sensorArray.getSensorSpacing();
        } else if (!signalArray[0] && !signalArray[1] && signalArray[2]) {    //  -   -   1
            error = sensorArray.getSensorSpacing() + sensorArray.getSensorWidth();
        } else {
            throw new EStopException("Critical error in sensor states");
        }

        System.out.println(signalArray[0] + " " + signalArray[1] + " " + signalArray[2]);
    }

    /**
     * Calculates the PID value (horizontal error) based on sensor error values
     */
    public int calculatePID() {
        int proportional = getKp() * error;
        int integral = getKi() * errorSum;
        int derivative = getKd() * previousError;
        pidValue = proportional + integral + derivative;
        if (pidValue > 180) {
            pidValue = 180;
        }
        errorSum += error;
        previousError = error;
        System.out.println(pidValue);
        return pidValue;
    }

    /**
     * Calculates the adjustment angle based off of the current PID value (horizontal error)
     * and the yDistance of the system
     *
     * @return the angle the system needs to turn in order to stay on the wire path
     */
    public double getErrorAngle() {
        return -(Math.atan(getPidValue() / sensorArray.getYDistance()));
    }

}
