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
            ObstacleMessage receivedMessage = server.receiveRawDataMessage();
            if (receivedMessage == null) {
                continue;
            }
            System.out.println(receivedMessage);
            //TODO: React to message (move izzy accordingly)
            /*
            Steps:
                if (not acknowledged && obstacle detected)
                    stop followLine();
                    acknowledge; --> will involve sending message back. I'd extend RPLidarServer class to include this
                else if (acknowledged && obstacle detected)
                    izzyTurnIncrement(90 - angle) --> trying to get object at 90 degrees
                    if (lineDetected)
                        sendLineDetectedMessage();
                else if (no obstacle detected)
                    continue;
                    //TODO: figure out what happens if we don't have line, but we lose obstacle (actor walks away)
             */
        }
        server.stopServer();
    }
}
