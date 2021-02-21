package LineFollowing;

import util.Direction;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package LineFollowing
 * @date 12/4/2018
 */
public class LineFollower {

    private int velocity;
    private Direction direction;
    private int target;

    public LineFollower() {
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
