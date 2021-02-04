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

    public LineFollowControlThread(final AtomicBoolean running, final PIDCalculations pidCalculations,
                                   final SensorArray sensorArray, final AtomicBoolean moving,
                                   final IZZYMoveLineFollow izzyMove, final AtomicInteger speed) {
        this.running = running;
        this.pidCalculations = pidCalculations;
        this.sensorArray = sensorArray;
        this.moving = moving;
        this.izzyMove = izzyMove;
        this.speed = speed;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
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