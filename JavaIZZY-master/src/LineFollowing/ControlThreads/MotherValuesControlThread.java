package LineFollowing.ControlThreads;

import LineFollowing.PIDCalculations;
import LineFollowing.SensorArray;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MotherValuesControlThread implements Runnable {

    private final AtomicBoolean running;
    private final AtomicInteger speed;
    private final PIDCalculations pidCalculations;
    private final AtomicBoolean moving;
    private final SensorArray sensorArray;

    public MotherValuesControlThread(final AtomicBoolean running, final AtomicInteger speed,
                                     final PIDCalculations pidCalculations, final AtomicBoolean moving,
                                     final SensorArray sensorArray) {
        this.running = running;
        this.speed = speed;
        this.pidCalculations = pidCalculations;
        this.moving = moving;
        this.sensorArray = sensorArray;
    }

    @Override
    public void run() {
        try {
            InetAddress outgoingAddress = InetAddress.getByName("192.168.2.3");
            int outgoingPort = 8000;
            while (running.get()) {
                try {
                    OSCMessage outgoingMessage = new OSCMessage();
                    outgoingMessage.setAddress("/IZZYMother/Status");
                    outgoingMessage.addArgument(speed.get());
                    outgoingMessage.addArgument(pidCalculations.getPidValue());
                    outgoingMessage.addArgument(pidCalculations.getErrorAngle());
                    outgoingMessage.addArgument(pidCalculations.getKp());
                    outgoingMessage.addArgument(pidCalculations.getKi());
                    outgoingMessage.addArgument(pidCalculations.getKd());
                    outgoingMessage.addArgument(moving.get());
                    outgoingMessage.addArgument("Not Implemented");
                    outgoingMessage.addArgument(sensorArray.readSensors()[0]);
                    outgoingMessage.addArgument(sensorArray.readSensors()[1]);
                    outgoingMessage.addArgument(sensorArray.readSensors()[2]);
                    OSCPortOut sender = new OSCPortOut(outgoingAddress, outgoingPort);
                    sender.send(outgoingMessage);
                    sender.close();

                    System.out.println(sensorArray.readSensors()[0] + " " + sensorArray.readSensors()[1] + " " + sensorArray.readSensors()[2]);
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("The communication interface has stopped looping");
            e.printStackTrace();
        }
    }
}

