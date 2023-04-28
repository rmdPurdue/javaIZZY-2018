package com.rmdPurdue.izzyRobot.hardware.lineFollowing;

import com.rmdPurdue.izzyRobot.exceptions.EStopException;
import com.rmdPurdue.izzyRobot.exceptions.LostWireException;
import com.rmdPurdue.izzyRobot.exceptions.MotionStopException;

public class PIDCalculations {
    private double error; //the stored value for the current error for the PID loop P function (in mm)
    private double errorSum; //the combined value of all previous errors for the PID loop I function (in mm)
    private double previousError; //the stored value of the previous error for the PID loop D function (in mm)
    private double kp; //the ratio of the effect of the P value in the PID loop, adjusted for tuning
    private double ki; //the ratio of the effect of the I value in the PID loop, adjusted for tuning
    private double kd; //the ratio of the effect of the D value in the PID loop, adjusted for tuning
    private double pidValue; //the stored PID value for the horizontal error
    private final SensorArray sensorArray;
    private int numberOfNoDetects;

    public PIDCalculations(final int kp, final int ki, final int kd, final SensorArray sensorArray) {
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
    public double getKp() {
        return this.kp;
    }

    /**
     * Sets the kP value
     *
     * @param kp kP value of the PID loop
     */
    public void setKp(final double kp) {
        this.kp = kp;
    }

    /**
     * Returns the kI value
     *
     * @return kI value of the PID loop
     */
    public double getKi() {
        return this.ki;
    }

    /**
     * Sets the kI value
     *
     * @param ki kI value of the PID loop
     */
    public void setKi(final double ki) {
        this.ki = ki;
    }

    /**
     * Returns kD value
     *
     * @return kD value of PID loop
     */
    public double getKd() {
        return this.kd;
    }

    /**
     * Sets the kD value
     *
     * @param kd kD value of the PID loop
     */
    public void setKd(final double kd) {
        this.kd = kd;
    }

    /**
     * Returns PID value after calculating (call readSensors and calculatePID first)
     *
     * @return PID value (horizontal error)
     */
    public double getPidValue() {
        return pidValue;
    }

    public boolean isLineDetected() throws EStopException {
        final boolean[] signalArray = sensorArray.readSensorsBoolean();
        return signalArray[0] || signalArray[1];
    }

    /**
     * Calculates the current error as read by the sensors
     *
     * Throws a motionStop exception if all three sensors are reading the same value (+ or -)
     *
     * Throws an eStop exception if there is a catastrophic failure
     */
    public void adjustError() throws MotionStopException, EStopException {
        final boolean[] signalArray = sensorArray.readSensorsBoolean();
        if (signalArray[0] && signalArray[1]) {             //  1   1
            error = 0;
        } else if (!signalArray[0] && !signalArray[1]) {    //  -   -
            error = 0;
        } else if (signalArray[0] && !signalArray[1]) {     //  1   -
            error = -(sensorArray.getSensorWidth() + sensorArray.getSensorSpacing());
        } else if (!signalArray[0] && signalArray[1]) {     //  -   1
            error = sensorArray.getSensorSpacing() + sensorArray.getSensorWidth();
        } else {
            throw new EStopException("Critical error in sensor states");
        }
    }

    public void adjustErrorAnalog() throws MotionStopException, EStopException {
        try {
            error = sensorArray.calculateDistance();
        } catch (LostWireException lwe) {
            numberOfNoDetects++;
        }
        if (numberOfNoDetects >= 5) {
            throw new MotionStopException("Lost Wire");
        }
        error = sensorArray.calculateDistance();
    }

    /**
     * Calculates the PID value (horizontal error) based on sensor error values
     */
    public double calculatePID() {
        double proportional = getKp() * error;
        double integral = getKi() * errorSum;
        double derivative = getKd() * (error - previousError);
        pidValue = proportional + integral + derivative;
        errorSum += error;
        previousError = error;
        return pidValue;
    }

    /**
     * Calculates the adjustment angle based off of the current PID value (horizontal error)
     * and the yDistance of the system
     *
     * @return the angle the system needs to turn in order to stay on the wire path
     */
    public double getErrorAngle() {
        //Math.atan returns in radians. We need degrees. Conversion: Radians * 180 / PI
        return (-(Math.atan(getPidValue() / sensorArray.getYDistance())) * 180) / Math.PI;
    }

    public void resetSystem() {
        error = 0;
        errorSum = 0;
        previousError = 0;
        kp = 1;
        ki = 0;
        kd = 0;
        pidValue = 0;
    }
}
