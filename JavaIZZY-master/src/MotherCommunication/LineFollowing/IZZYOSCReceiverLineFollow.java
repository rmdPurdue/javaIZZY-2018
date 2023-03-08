package MotherCommunication.LineFollowing;

import MotherCommunication.IZZYOSCReceiver;
import Movement.LineFollowing.IZZYMoveLineFollow;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import lombok.extern.log4j.Log4j2;

import java.net.SocketException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static MotherCommunication.LineFollowing.OSCAddresses.*;

@Log4j2
public class IZZYOSCReceiverLineFollow extends IZZYOSCReceiver {

    private final AtomicBoolean isRunning;
    private final IZZYMoveLineFollow izzyMoveLineFollow;

    // Listen for stop / start commands
    private final OSCListener followLineStateListener = (time, motherMessage) -> {
        log.debug("Line State Message received from Mother!");
        parseFollowLineStateOSC(motherMessage);
    };
    // Listen for speed values
    private final OSCListener followLineSpeedListener = (time, motherMessage) -> {
        log.debug("Line Speed Message received from Mother!");
        parseFollowLineSpeedOSC(motherMessage);
    };
    // Listen for Kp, Ki, and Kd values
    private final OSCListener followLineTuneListener = (time, motherMessage) -> {
        log.debug("Line Tune Message received from Mother!");
        parseFollowLineTuneOSC(motherMessage);
    };
    //Listen for sensor threshold values
    private final OSCListener followLineThresholdListener = (time, motherMessage) -> {
        log.debug("Sensor Threshold Message received from Mother!");
        parseFollowLineThresholdOSC(motherMessage);
    };
    //Listen for StopProcessing Signal
    private final OSCListener stopProcessingListener = (time, motherMessage) -> {
        log.debug("End Processing");
        parseStopProcessing(motherMessage);
    };
    //Listen for ResetSystem Signal
    private final OSCListener resetSystemListener = (time, motherMessage) -> {
        log.debug("End Processing");
        parseResetSystem(motherMessage);
    };
    // Listen for eStop messages
    private final OSCListener eStopListener = (time, motherMessage) -> {
        log.debug("ESTOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        eStopOSC(motherMessage);
    };
    // Listen for sensor range adjustments
    private final OSCListener sensorRangeListener = (time, motherMessage) -> {
        log.debug("Sensor Ranges");
        parseSensorRangeOSC(motherMessage);
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
        super.addListener(SET_SENSOR_RANGES.valueOf(), sensorRangeListener);

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

    private void parseSensorRangeOSC(final OSCMessage msg) {
        if (msg == null) {
            return;
        }
        List<Object> msgArgs = msg.getArguments();
        if (msgArgs != null && msgArgs.size() == 4) {
            izzyMoveLineFollow.setSensorRanges((int) msgArgs.get(0), (int) msgArgs.get(1), (int) msgArgs.get(2), (int) msgArgs.get(3));
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

