package ControlThreads;

import MotherCommunication.Heartbeat.HeartbeatResponder;
import MotherCommunication.Heartbeat.HeartbeatResponseListener;
import MotherCommunication.Heartbeat.MotherStatus;
import MotherCommunication.LineFollowing.IZZYOSCSenderLineFollow;
import Movement.LineFollowing.IZZYMoveLineFollow;

import java.util.concurrent.atomic.AtomicBoolean;

public class LineFollowControlThread implements Runnable, HeartbeatResponseListener {

    private final AtomicBoolean running;
    private final IZZYMoveLineFollow izzyMove;
    private final HeartbeatResponder heartBeat;
    private final IZZYOSCSenderLineFollow izzyoscSenderLineFollow;
    private boolean isHeartBeating;

    public LineFollowControlThread(final AtomicBoolean running, final IZZYMoveLineFollow izzyMove,
                                   final HeartbeatResponder heartBeat,
                                   final IZZYOSCSenderLineFollow izzyoscSenderLineFollow) {
        this.running = running;
        this.isHeartBeating = false;
        this.izzyMove = izzyMove;
        this.heartBeat = heartBeat;
        this.izzyoscSenderLineFollow = izzyoscSenderLineFollow;
    }

    @Override
    public void run() {
        try {
            this.heartBeat.setListener(this);
            while (running.get()) {
                if (!isHeartBeating) {
                    System.out.println("Heart Not Beating");
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
                    System.out.println(e.getMessage());
                }
                Thread.sleep(100);
            }
            izzyMove.stop();
        } catch (final Exception e) {
            System.out.println("The control interface has stopped looping");
            e.printStackTrace();
        } finally {
            System.out.println("The control interface has stopped looping");
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
