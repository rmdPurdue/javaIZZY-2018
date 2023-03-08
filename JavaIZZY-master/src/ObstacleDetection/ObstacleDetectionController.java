package ObstacleDetection;

import Movement.LineFollowing.IZZYMoveLineFollow;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
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
            short[] receivedMessage = server.receiveMessage();
            if (receivedMessage == null) {
                continue;
            }
            short flaggedMessage = receivedMessage[361];
            System.out.println(Arrays.toString(receivedMessage));
            //int unsigned = Short.toUnsignedInt(flaggedMessage);

//            if ((unsigned & 255) > 0) { // if any of the first 8 bits are 1 (detected within 200mm)
//                izzyMove.setIsMoving(false);
//                log.info("STOPPED");
//            }
//            // if any of the second 8 bits are 1 (detected within 400mm)
//            dangerApproaching.set((unsigned & 65280) > 0);
        }
        server.stopServer();
    }
}
