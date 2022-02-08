package MotherCommunication.LineFollowing;

import MotherCommunication.IZZYOSCReceiver;
import Movement.LineFollowing.IZZYMoveLineFollow;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import java.net.SocketException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static MotherCommunication.LineFollowing.OSCAddresses.*;

public class IZZYOSCReceiverLineFollow extends IZZYOSCReceiver {

    private final AtomicBoolean isRunning;
    private final IZZYMoveLineFollow izzyMoveLineFollow;

    // Listen for stop / start commands
    private final OSCListener followLineStateListener = (time, motherMessage) -> {
        System.out.println("Line State Message received from Mother!");
        parseFollowLineStateOSC(motherMessage);
    };
    // Listen for speed values
    private final OSCListener followLineSpeedListener = (time, motherMessage) -> {
        System.out.println("Line Speed Message received from Mother!");
        parseFollowLineSpeedOSC(motherMessage);
    };
    // Listen for Kp, Ki, and Kd values
    private final OSCListener followLineTuneListener = (time, motherMessage) -> {
        System.out.println("Line Tune Message received from Mother!");
        parseFollowLineTuneOSC(motherMessage);
    };
    //Listen for sensor threshold values
    private final OSCListener followLineThresholdListener = (time, motherMessage) -> {
        System.out.println("Sensor Threshold Message received from Mother!");
        parseFollowLineThresholdOSC(motherMessage);
    };
    //Listen for StopProcessing Signal
    private final OSCListener stopProcessingListener = (time, motherMessage) -> {
        System.out.println("End Processing");
        parseStopProcessing(motherMessage);
    };
    //Listen for ResetSystem Signal
    private final OSCListener resetSystemListener = (time, motherMessage) -> {
        System.out.println("End Processing");
        parseResetSystem(motherMessage);
    };
    // Listen for eStop messages
    private final OSCListener eStopListener = (time, motherMessage) -> {
        System.out.println("ESTOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        eStopOSC(motherMessage);
    };

    public IZZYOSCReceiverLineFollow(final AtomicBoolean isRunning, final IZZYMoveLineFollow izzyMoveLineFollow)
                                            throws SocketException {
        super();
        super.addListener(FOLLOW_LINE_STATE.valueOf(), followLineStateListener);
        super.addListener(FOLLOW_LINE_SPEED.valueOf(), followLineSpeedListener);
        super.addListener(FOLLOW_LINE_TUNE.valueOf(), followLineTuneListener);
        super.addListener(FOLLOW_LINE_THRESHOLD.valueOf(), followLineThresholdListener);
        super.addListener(STOP_PROCESSING.valueOf(), stopProcessingListener);
        super.addListener(RESET_SYSTEM.valueOf(), resetSystemListener);
        super.addListener(FOLLOW_LINE_ESTOP.valueOf(), eStopListener);

        this.isRunning = isRunning;
        this.izzyMoveLineFollow = izzyMoveLineFollow;
    }

    private void parseFollowLineStateOSC(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 2) {
            if (msgArgs.get(0).equals("start")) {
                izzyMoveLineFollow.setIsMoving(true);
            } else if (msgArgs.get(0).equals("stop")) {
                izzyMoveLineFollow.setIsMoving(false);
            }
            izzyMoveLineFollow.setSpeedValue((int) msgArgs.get(1));
        }
    }

    private void parseFollowLineSpeedOSC(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 1) {
            izzyMoveLineFollow.setSpeedValue((int) msgArgs.get(0));
        }
    }

    private void parseFollowLineTuneOSC(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 3) {
            izzyMoveLineFollow.tunePidLoop((double) msgArgs.get(0), (double) msgArgs.get(1), (double) msgArgs.get(2));
        }
    }

    private void parseFollowLineThresholdOSC(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 3) {
            izzyMoveLineFollow.setSensorThresholds((int) msgArgs.get(0), (int) msgArgs.get(1), (int) msgArgs.get(2));
        }
    }

    private void parseStopProcessing(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 0) {
            stopListening();
            isRunning.set(false);
        }
    }

    private void parseResetSystem(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 0) {
            izzyMoveLineFollow.resetSystem();
        }
    }

    public void eStopOSC(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 1) {
            if (msgArgs.get(0).equals("eStop")) {
                izzyMoveLineFollow.eStop();
                isRunning.set(false);
            }
        }
    }

}

