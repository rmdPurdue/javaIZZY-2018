package LineFollowing;

import KangarooSimpleSerial.KangarooSimpleChannel;

public class IZZYMove {

    private KangarooSimpleChannel D;
    private KangarooSimpleChannel T;
    private int driveSpeed;
    private int angleSetPoint;

    /**
     * Creates instance of LineFollowing.IZZYMove class
     *
     * @param drive Drive channel ('D')
     * @param turn Turn channel ('T')
     * @param wheelRad radius of one wheel measured in mm
     * @param systemRad distance from one wheel center to the other / 2
     * @param encoderResolution the number of pulses per round
     * @param motorRatio the amount of gear turns per one wheel turn
     */
    public IZZYMove(KangarooSimpleChannel drive, KangarooSimpleChannel turn, double wheelRad, double systemRad, int encoderResolution, int motorRatio) {
        this.D = drive;
        this.T = turn;
        this.D.start();
        this.T.start();
        double readableDrive = (Math.PI * (wheelRad * 2)) * 10;
        double lineDrive = (encoderResolution * motorRatio) * 10;
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
        this.T.P(this.angleSetPoint);
    }

    /**
     * Turns IZZY an incremented angle
     *
     * @param angleIn incrementing angle in degrees
     */
    public void izzyTurnIncrement(int angleIn) {
        this.angleSetPoint += angleIn;
        this.T.PI(this.angleSetPoint);
    }

    /**
     * Moves IZZY at a specified speed.
     *
     * @param speed speed in mm/sec
     */
    public void izzyMove(int speed) {
        this.driveSpeed = speed;
        this.D.S(this.driveSpeed);
    }

    /**
     * Increments IZZY's speed by a specified amount.
     *
     * @param speed speed in mm/sec
     */
    public void izzyMoveIncrement(int speed) {
        this.driveSpeed += speed;
        this.D.SI(this.driveSpeed);
    }

    /**
     * IZZY adjusts movement based on lineFollowing inputs
     *
     * @param errorAngle the angle of error detected in the system
     * @param speed the speed at which IZZY is moving in mm/sec
     */
    public void followLine(int errorAngle, int speed) throws Exception {
        izzyMove(speed);
        izzyTurn(errorAngle);
    }

}
