package Hardware.LineFollowing;

import Hardware.Kanagaroo.KangarooSimpleSerial.KangarooSimpleChannel;
import Hardware.Kanagaroo.SerialDataEventListener;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DriveReadings implements SerialDataEventListener {

    private final KangarooSimpleChannel d;
    private final KangarooSimpleChannel t;
    private final Object kangarooSyncLock;
    private final AtomicBoolean isRunning;
    private final AtomicInteger driveP;
    private final AtomicInteger driveS;
    private final AtomicInteger turnP;
    private final AtomicInteger turnS;

    public DriveReadings(KangarooSimpleChannel d, KangarooSimpleChannel t, Object kangarooSyncLock, AtomicBoolean isRunning) {
        this.d = d;
        this.t = t;
        this.kangarooSyncLock = kangarooSyncLock;
        this.isRunning = isRunning;
        this.driveP = new AtomicInteger(-1);
        this.driveS = new AtomicInteger(-1);
        this.turnP = new AtomicInteger(-1);
        this.turnS = new AtomicInteger(-1);
    }

    public void startWheelReadingLoop() {
        synchronized (kangarooSyncLock) {
            d.getP();
            d.getS();
            t.getP();
            t.getS();
        }
    }

    public int getDriveP() {
        return driveP.get();
    }

    public void setDriveP(int driveP) {
        this.driveP.set(driveP);
    }

    public int getDriveS() {
        return driveS.get();
    }

    public void setDriveS(int driveS) {
        this.driveS.set(driveS);
    }

    public int getTurnP() {
        return turnP.get();
    }

    public void setTurnP(int turnP) {
        this.turnP.set(turnP);
    }

    public int getTurnS() {
        return turnS.get();
    }

    public void setTurnS(int turnS) {
        this.turnS.set(turnS);
    }

    @Override
    public void dataReceived(String data) {
        Pattern pattern = Pattern.compile("([DTdt]),([SPsp])(-?\\d+)");
        String[] readings = data.split("\n");
        for (String reading : readings) {
            Matcher matcher = pattern.matcher(reading);
            if (matcher.find()) {
                if (matcher.group(1).equals("D") || matcher.group(1).equals("d")) {
                    if (matcher.group(2).equals("S") || matcher.group(2).equals("s")) {
                        setDriveS(Integer.parseInt(matcher.group(3)));
                    } else { // P
                        setDriveP(Integer.parseInt(matcher.group(3)));
                    }
                } else { // T
                    if (matcher.group(2).equals("S") || matcher.group(2).equals("s")) {
                        setTurnS(Integer.parseInt(matcher.group(3)));
                        if (isRunning.get()) {
                            startWheelReadingLoop();
                        }
                    } else { // P
                        setTurnP(Integer.parseInt(matcher.group(3)));
                    }
                }
            } else {
                log.info("Bad data from Kangaroo: {}", data);
            }
        }
    }
}
