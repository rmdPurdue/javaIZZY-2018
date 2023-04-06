package com.rmdPurdue.izzyRobot.obstacleDetection;

import com.rmdPurdue.izzyRobot.movement.lineFollowing.IZZYMoveLineFollow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObstacleDetectionController implements Runnable {
    private static final Logger log = LogManager.getLogger(ObstacleDetectionController.class);

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
            short[] receivedMessage = server.receiveRawDataMessage();
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
