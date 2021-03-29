package LineFollowing.ControlThreads;

import LineFollowing.IZZYMoveLineFollow;
import LineFollowing.PIDCalculations;
import LineFollowing.SensorArray;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LineFollowControlThread implements Runnable {

    private final AtomicBoolean running;
    private final PIDCalculations pidCalculations;
    private final SensorArray sensorArray;
    private final AtomicBoolean moving;
    private final IZZYMoveLineFollow izzyMove;
    private final AtomicInteger speed;
    private final AtomicBoolean isHeartBeating;

    public LineFollowControlThread(final AtomicBoolean running, final PIDCalculations pidCalculations,
                                   final SensorArray sensorArray, final AtomicBoolean moving,
                                   final IZZYMoveLineFollow izzyMove, final AtomicInteger speed,
                                   final AtomicBoolean isHeartBeating) {
        this.running = running;
        this.pidCalculations = pidCalculations;
        this.sensorArray = sensorArray;
        this.moving = moving;
        this.izzyMove = izzyMove;
        this.speed = speed;
        this.isHeartBeating = isHeartBeating;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                if (!isHeartBeating.get()) {
                    System.out.println("Heart Not Beating");
                    Thread.sleep(100);
                    continue;
                }
                try {
                    pidCalculations.adjustError(sensorArray.readSensors());
                    if (moving.get()) {
                        pidCalculations.calculatePID();
                        izzyMove.followLine((int) (pidCalculations.getErrorAngle() + 0.5), speed.get());
                    }
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }
                if (!moving.get()) {
                    izzyMove.followLine((int) (pidCalculations.getErrorAngle() + 0.5), 0);
                }

                Thread.sleep(100);
            }
        } catch (final Exception e) {
            System.out.println("The control interface has stopped looping");
            e.printStackTrace();
        } finally {
            System.out.println("The control interface has stopped looping");
        }
    }
}