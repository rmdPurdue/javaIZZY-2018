package ControlThreads;

import MotherCommunication.Heartbeat.HeartbeatResponder;
import MotherCommunication.Heartbeat.MessageType;
import MotherCommunication.LineFollowing.IZZYOSCSenderLineFollow;

import java.util.concurrent.atomic.AtomicBoolean;

public class MotherValuesControlThread implements Runnable {

    private final AtomicBoolean isRunning;
    private final IZZYOSCSenderLineFollow izzyoscSenderLineFollow;
    private final HeartbeatResponder heartBeat;

    public MotherValuesControlThread(AtomicBoolean isRunning, IZZYOSCSenderLineFollow izzyoscSenderLineFollow,
                                     HeartbeatResponder heartBeat) {
        this.isRunning = isRunning;
        this.izzyoscSenderLineFollow = izzyoscSenderLineFollow;
        this.heartBeat = heartBeat;
    }

    @Override
    public void run() {
        int errorCount = 0;
        while (isRunning.get()) {
            try {
                izzyoscSenderLineFollow.sendData();
                Thread.sleep(200);
                errorCount = 0;
            } catch (Exception e) {
                // Swallow exception to prevent logic stop for feedback error unless 3 consecutive errors
                errorCount++;
                if (errorCount > 3) {
                    izzyoscSenderLineFollow.close();
                    heartBeat.setMessageType(MessageType.OSC_COM_ERROR);
                    heartBeat.setErrorMessage(e.getMessage());
                    isRunning.set(false);
                }
            }
        }
    }
}

