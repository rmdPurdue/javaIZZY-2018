package com.rmdPurdue.izzyRobot.movement.lineFollowing;

import com.rmdPurdue.izzyRobot.exceptions.EStopException;
import com.rmdPurdue.izzyRobot.exceptions.MotionStopException;
import com.rmdPurdue.izzyRobot.hardware.kanagaroo.kangarooSimpleSerial.KangarooSimpleChannel;
import com.rmdPurdue.izzyRobot.hardware.lineFollowing.DriveReadings;
import com.rmdPurdue.izzyRobot.hardware.lineFollowing.PIDCalculations;
import com.rmdPurdue.izzyRobot.hardware.lineFollowing.SensorArray;
import com.rmdPurdue.izzyRobot.movement.IZZYMove;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IZZYMoveLineFollow extends IZZYMove {

    private final AtomicInteger speedValue;
    private final AtomicBoolean isMoving;
    private final PIDCalculations pidCalculations; // monitor sensors and automate movement
    private final SensorArray sensorArray;
    private final AtomicBoolean dangerApproaching; // triggered if object getting close
    private final DriveReadings driveReadings;

    /**
     * Creates instance of IZZYMovement.IZZYMove class
     *
     * @param drive             Drive channel ('D')
     * @param turn              Turn channel ('T')
     * @param sensorArray       Array of sensors to detect line movement
     * @param wheelRad          radius of one wheel measured in mm
     * @param systemRad         distance from one wheel center to the other / 2
     * @param encoderResolution the number of pulses per round
     * @param motorRatio        the amount of gear turns per one wheel turn
     */
    public IZZYMoveLineFollow(final KangarooSimpleChannel drive, final KangarooSimpleChannel turn,
                              final SensorArray sensorArray, final double wheelRad, final double systemRad,
                              final int encoderResolution, final int motorRatio,
                              final AtomicBoolean dangerApproaching, final Object kangarooSyncLock,
                              final DriveReadings driveReadings) {
        super(drive, turn, wheelRad, systemRad, encoderResolution, motorRatio, kangarooSyncLock, driveReadings);
        speedValue = new AtomicInteger(0);
        isMoving = new AtomicBoolean(false);
        this.sensorArray = sensorArray;
        this.pidCalculations = new PIDCalculations(1, 0, 0, sensorArray);
        this.dangerApproaching = dangerApproaching;
        this.driveReadings = driveReadings;
    }

    /**
     * IZZY adjusts movement based on sensor inputs and current speed value
     */
    public void followLine() throws EStopException, MotionStopException {
        pidCalculations.adjustErrorAnalog();
        pidCalculations.calculatePID();
        if (dangerApproaching.get()) {
            izzyMove(speedValue.get() / 2);
        } else {
            izzyMove(speedValue.get());
        }
        izzyTurnIncrement((int) (-pidCalculations.getErrorAngle() + 0.5), 90);
    }

    public void setSpeedValue(final int speed) {
        speedValue.set(speed);
    }

    public int getSpeedValue() {
        return speedValue.get();
    }

    public boolean isMoving() {
        return isMoving.get();
    }

    public void setIsMoving(boolean isMoving) {
        this.isMoving.set(isMoving);
    }

    public void tunePidLoop(double kp, double ki, double kd) {
        pidCalculations.setKp(kp);
        pidCalculations.setKi(ki);
        pidCalculations.setKd(kd);
    }

    public double getPidValue() {
        return pidCalculations.getPidValue();
    }

    public double getErrorAngle() {
        return pidCalculations.getErrorAngle();
    }

    public double getKp() {
        return pidCalculations.getKp();
    }

    public double getKi() {
        return pidCalculations.getKi();
    }

    public double getKd() {
        return pidCalculations.getKd();
    }

    public int[] getSensorsAnalog() throws EStopException {
        if (!isMoving.get()) {
            return sensorArray.readSensorsAnalog();
        }
        return sensorArray.getSensorsAnalog();
    }

    public int[] getSensorThresholds() {
        return sensorArray.getSensorThresholds();
    }

    public void setSensorThresholds(int leftThreshold, int centerThreshold, int rightThreshold) {
        sensorArray.setSensorThresholds(leftThreshold, centerThreshold, rightThreshold);
    }

    public void setSensorRanges(int sensor0Min, int sensor0Max, int sensor1Min, int sensor1Max) {
        sensorArray.setSensorRanges(sensor0Min, sensor0Max, sensor1Min, sensor1Max);
    }

    public void resetSystem() {
        pidCalculations.resetSystem();
        super.resetKangaroo();
        speedValue.set(25);
    }

    public void stop() {
        izzyMove(0);
        izzyTurn((int) (-pidCalculations.getErrorAngle() + 0.5));
    }
}
