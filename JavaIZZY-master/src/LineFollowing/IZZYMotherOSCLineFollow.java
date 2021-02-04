package LineFollowing;

import IZZYMotherCommunication.IZZYMotherOSC;
import KangarooSimpleSerial.KangarooSimpleChannel;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import java.net.SocketException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IZZYMotherOSCLineFollow extends IZZYMotherOSC {

    private final AtomicBoolean moving;
    private final AtomicInteger speed;
    private final PIDCalculations pidCalculations;
    private final AtomicBoolean running;
    private final KangarooSimpleChannel D;
    private final KangarooSimpleChannel T;

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
    //Listen for StopProcessing Signal
    private final OSCListener stopProcessingListener = (time, motherMessage) -> {
        System.out.println("End Processing");
        parseStopProcessing(motherMessage);
    };
    // Listen for eStop messages
    private final OSCListener eStopListener = (time, motherMessage) -> {
        System.out.println("ESTOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        eStopOSC(motherMessage);
    };

    public IZZYMotherOSCLineFollow(final AtomicBoolean moving, final AtomicInteger speed,
                                   final PIDCalculations pidCalculations, final AtomicBoolean running,
                                   final KangarooSimpleChannel D, final KangarooSimpleChannel T)
                                            throws SocketException {
        super();
        super.addListener("/IZZY/FollowLineState", followLineStateListener);
        super.addListener("/IZZY/FollowLineSpeed", followLineSpeedListener);
        super.addListener("/IZZY/FollowLineTune", followLineTuneListener);
        super.addListener("/IZZY/StopProcessing", stopProcessingListener);
        super.addListener("/IZZY/eStop", eStopListener);
        this.moving = moving;
        this.speed = speed;
        this.pidCalculations = pidCalculations;
        this.running = running;
        this.D = D;
        this.T = T;
    }

    private void parseFollowLineStateOSC(final OSCMessage msg) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/FollowLineState")) {
            if (msgArgs.get(0).equals("start")) {
                moving.set(true);
            } else if (msgArgs.get(0).equals("stop")) {
                moving.set(false);
            }
            speed.set((int) msgArgs.get(1));
        }
    }

    private void parseFollowLineSpeedOSC(final OSCMessage msg) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/FollowLineSpeed")) {
            speed.set((int) msgArgs.get(0));
        }
    }

    private void parseFollowLineTuneOSC(final OSCMessage msg) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/FollowLineTune")) {
            pidCalculations.setKp((double) msgArgs.get(0));
            pidCalculations.setKi((double) msgArgs.get(1));
            pidCalculations.setKd((double) msgArgs.get(2));
        }
    }

    private void parseStopProcessing(final OSCMessage msg) {
        if(msg.getAddress().equals("/IZZY/StopProcessing")) {
            running.set(false);
            moving.set(false);
        }
    }

    public void eStopOSC(final OSCMessage msg) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/eStop")) {
            if (msgArgs.get(0).equals("eStop")) {
                running.set(false);
                moving.set(false);
                speed.set(0);
                D.powerDown();
                T.powerDown();
            }
        }
    }

}

