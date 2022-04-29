package ControlThreads;

import MotherCommunication.Heartbeat.HeartbeatResponder;
import MotherCommunication.Heartbeat.HeartbeatResponseListener;
import MotherCommunication.Heartbeat.MotherStatus;
import Movement.LineFollowing.IZZYMoveLineFollow;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class LineFollowControlThread implements Runnable, HeartbeatResponseListener {

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
                    Thread.sleep(100);
                    continue;
                }
                try {
                    if (izzyMove.isMoving()) {
                        izzyMove.followLine();
                    } else {
                        izzyMove.izzyMove(0);
                    }
                } catch (final Exception e) {
                    log.error(e.getMessage());
                }
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
