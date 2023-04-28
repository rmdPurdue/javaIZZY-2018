package com.rmdPurdue.izzyRobot.obstacleDetection;

import com.rmdPurdue.izzyRobot.exceptions.EStopException;
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

            if (!receivedMessage.isObstacleAcknowledged() && receivedMessage.isObstacleDetected()) {
                izzyMove.setStopFollowLine(true);
                server.sendAcknowledgedMessage();
                izzyMove.izzyTurnIncrement((int) (90 - receivedMessage.getAngle() + 0.5));
            } else if (receivedMessage.isObstacleAcknowledged() && receivedMessage.isObstacleDetected()) {
                izzyMove.izzyTurnIncrement((int) (90 - receivedMessage.getAngle() + 0.5));
                try {
                    if (izzyMove.isLineDetected()) {
                        server.sendDetectedMessage();
                        izzyMove.setStopFollowLine(false);
                    }
                } catch (EStopException e) {
                    throw new RuntimeException(e);
                }
            }
            //TODO: figure out what happens if we don't have line, but we lose obstacle (actor walks away)
        }
        server.stopServer();
    }
}
