package com.rmdPurdue.izzyRobot.controlThreads;

import com.rmdPurdue.izzyRobot.Main;
import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.HeartbeatResponder;
import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.HeartbeatResponseListener;
import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.MotherStatus;
import com.rmdPurdue.izzyRobot.movement.lineFollowing.IZZYMoveLineFollow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class LineFollowControlThread implements Runnable, HeartbeatResponseListener {
    private static final Logger log = LogManager.getLogger(LineFollowControlThread.class);

    private final AtomicBoolean running;
    private final IZZYMoveLineFollow izzyMove;
    private final HeartbeatResponder heartBeat;
    private boolean isHeartBeating;

    public LineFollowControlThread(final AtomicBoolean running, final IZZYMoveLineFollow izzyMove,
                                   final HeartbeatResponder heartBeat) {
        this.running = running;
        this.isHeartBeating = false;
        this.izzyMove = izzyMove;
        this.heartBeat = heartBeat;
    }

    @Override
    public void run() {
        try {
            this.heartBeat.setListener(this);
            while (running.get()) {
                if (!isHeartBeating) {
                    log.debug("Heart Not Beating");
                    Thread.sleep(500);
                    continue;
                }
                try {
                    if (izzyMove.isMoving()) {
                        izzyMove.followLine();
                    } else {
                        izzyMove.izzyMove(0);
                        izzyMove.izzyTurnFreeze();
                    }
                } catch (final Exception e) {
                    log.error(e.getMessage());
                }
                Thread.sleep(100);
            }
            izzyMove.stop();
        } catch (final Exception e) {
            log.error("The control interface has stopped looping");
            e.printStackTrace();
        } finally {
            log.debug("The control interface has stopped looping");
        }
    }

    @Override
    public void onRemoteDeviceResponseReceived(MotherStatus motherStatus) {
        if (motherStatus.equals(MotherStatus.CONNECTED)) {
            isHeartBeating = true;
        }
    }

    @Override
    public void onMotherDeviceTimeout() {
        isHeartBeating = false;
    }
}
