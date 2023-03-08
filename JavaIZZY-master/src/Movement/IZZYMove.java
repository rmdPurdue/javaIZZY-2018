package Movement;

import Hardware.Kanagaroo.KangarooSimpleSerial.KangarooSimpleChannel;
import Hardware.LineFollowing.DriveReadings;

public abstract class IZZYMove {

    private final KangarooSimpleChannel D;
    private final KangarooSimpleChannel T;
    private int driveSpeed;
    private int angleSetPoint;
    private double wheelRad;
    private double systemRad;
    private int encoderResolution;
    private int motorRatio;
    private final Object kangarooSyncLock;
    private final DriveReadings driveReadings;

    /**
     * Creates instance of IZZYMovement.IZZYMove class
     *
     * @param drive Drive channel ('D')
     * @param turn Turn channel ('T')
     * @param wheelRad radius of one wheel measured in mm
     * @param systemRad distance from one wheel center to the other / 2
     * @param encoderResolution the number of pulses per round
     * @param motorRatio the amount of gear turns per one wheel turn
     */
    public IZZYMove(final KangarooSimpleChannel drive, final KangarooSimpleChannel turn, final double wheelRad,
                    final double systemRad, final int encoderResolution, final int motorRatio,
                    final Object kangarooSyncLock, final DriveReadings driveReadings) {
        this.kangarooSyncLock = kangarooSyncLock;
        this.wheelRad = wheelRad;
        this.systemRad = systemRad;
        this.encoderResolution = encoderResolution;
        this.motorRatio = motorRatio;
        this.driveReadings = driveReadings;
        this.D = drive;
        this.T = turn;
        this.D.start();
        this.T.start();
        double readableDrive = (Math.PI * (wheelRad * 2));
        double lineDrive = (encoderResolution * motorRatio);
        double lineAngle = Math.PI * (systemRad * 2) / readableDrive * lineDrive;
        this.D.units( (int)(readableDrive + 0.5) + " mm = " + (int)(lineDrive + 0.5) + " lines");
        this.T.units("360 degrees = " + (int)(lineAngle + 0.5) + " lines");
        this.D.S(0);
        this.T.P(0);
    }

    /**
     * Turns IZZY to specified angle based on current position
     *
     * @param angleIn set angle in degrees
     */
    public void izzyTurn(int angleIn) {
        this.angleSetPoint = angleIn;
        synchronized (kangarooSyncLock) {
            this.T.P(this.angleSetPoint);
        }
    }

    /**
     * Turns IZZY to specified angle based on current position at a specified speed
     *
     * @param angleIn set angle in degrees
     */
    public void izzyTurn(int angleIn, int speed) {
        this.angleSetPoint = angleIn;
        synchronized (kangarooSyncLock) {
            this.T.P(this.angleSetPoint, speed);
        }
    }

    /**
     * Turns IZZY an incremented angle
     *
     * @param angleIn incrementing angle in degrees
     */
    public void izzyTurnIncrement(int angleIn, int speed) {
        this.angleSetPoint += angleIn;
        synchronized (kangarooSyncLock) {
            this.T.PI(angleIn, speed);
        }
    }

    /**
     * Turns IZZY an incremented angle
     *
     * @param angleIn incrementing angle in degrees
     */
    public void izzyTurnIncrement(int angleIn) {
        this.angleSetPoint += angleIn;
        synchronized (kangarooSyncLock) {
            this.T.PI(angleIn);
        }
    }

    /**
     * Sets IZZY's turn value to the current angle. Essentially freezing IZZY in place
     */
    public void izzyTurnFreeze() {
        izzyTurn(driveReadings.getTurnP());
    }

    /**
     * Moves IZZY at a specified speed.
     *
     * @param speed speed in mm/sec
     */
    public void izzyMove(int speed) {
        if (this.driveSpeed == speed) {
            return;
        }
        this.driveSpeed = speed;
        synchronized (kangarooSyncLock) {
            this.D.S(this.driveSpeed);
        }
    }

    /**
     * Increments IZZY's speed by a specified amount.
     *
     * @param speed speed in mm/sec
     */
    public void izzyMoveIncrement(int speed) {
        this.driveSpeed += speed;
        synchronized (kangarooSyncLock) {
            this.D.SI(this.driveSpeed);
        }
    }

    public void eStop() {
        synchronized (kangarooSyncLock) {
            this.D.powerDown();
            this.T.powerDown();
        }
    }

    public void resetKangaroo() {
        synchronized (kangarooSyncLock) {
            this.D.powerDown();
            this.T.powerDown();
            this.D.start();
            this.T.start();
            double readableDrive = (Math.PI * (wheelRad * 2));
            double lineDrive = (encoderResolution * motorRatio);
            double lineAngle = Math.PI * (systemRad * 2) / readableDrive * lineDrive;
            this.D.units((int) (readableDrive + 0.5) + " mm = " + (int) (lineDrive + 0.5) + " lines");
            this.T.units("360 degrees = " + (int) (lineAngle + 0.5) + " lines");
//            this.D.S(0);
//            this.T.P(0);
        }
    }

}
