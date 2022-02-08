package ObstacleDetection;

import Movement.LineFollowing.IZZYMoveLineFollow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObstacleDetectionController implements Runnable {

    AtomicBoolean isRunning;
    IZZYMoveLineFollow izzyMove;
    RPLidarServer server;
    AtomicBoolean dangerApproaching;

    public ObstacleDetectionController(AtomicBoolean isRunning, IZZYMoveLineFollow izzyMove, AtomicBoolean dangerApproaching) throws IOException {
        this.isRunning = isRunning;
        this.izzyMove = izzyMove;
        this.server = new RPLidarServer(7005);
        this.dangerApproaching = dangerApproaching;
    }

    @Override
    public void run() {
        while(isRunning.get()) {
            short flaggedMessage = server.receiveMessage()[361];
            int unsigned = Short.toUnsignedInt(flaggedMessage);
            if ((unsigned & 255) > 0) { // if any of the first 8 bits are 1 (detected within 200mm)
                izzyMove.setIsMoving(false);
            }
            // if any of the second 8 bits are 1 (detected within 400mm)
            dangerApproaching.set((unsigned & 65280) > 0);
        }
        server.stopServer();
    }
}
