package LineFollowing;

import IZZYMovement.IZZYMove;
import KangarooSimpleSerial.KangarooSimpleChannel;

public class IZZYMoveLineFollow extends IZZYMove {

    /**
     * Creates instance of IZZYMovement.IZZYMove class
     *
     * @param drive             Drive channel ('D')
     * @param turn              Turn channel ('T')
     * @param wheelRad          radius of one wheel measured in mm
     * @param systemRad         distance from one wheel center to the other / 2
     * @param encoderResolution the number of pulses per round
     * @param motorRatio        the amount of gear turns per one wheel turn
     */
    public IZZYMoveLineFollow(final KangarooSimpleChannel drive, final KangarooSimpleChannel turn,
                              final double wheelRad, final double systemRad, final int encoderResolution,
                              final int motorRatio) {
        super(drive, turn, wheelRad, systemRad, encoderResolution, motorRatio);
    }

    /**
     * IZZY adjusts movement based on lineFollowing inputs
     *
     * @param errorAngle the angle of error detected in the system
     * @param speed the speed at which IZZY is moving in mm/sec
     */
    public void followLine(final int errorAngle, final int speed) {
        izzyMove(speed);
        izzyTurn(-errorAngle);
    }
}
