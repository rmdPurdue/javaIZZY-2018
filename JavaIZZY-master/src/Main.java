import Hardware.AD1115.ADS1115;
import Hardware.AD1115.ADS1115Builder;
import Hardware.LineFollowing.DriveReadings;
import Hardware.LineFollowing.LineSensor;
import Hardware.LineFollowing.SensorArray;
import MotherCommunication.Heartbeat.HeartbeatResponder;
import MotherCommunication.Heartbeat.MessageType;
import MotherCommunication.LineFollowing.IZZYOSCReceiverLineFollow;
import MotherCommunication.LineFollowing.IZZYOSCSenderLineFollow;
import Movement.LineFollowing.IZZYMoveLineFollow;
import Hardware.Kanagaroo.KangarooSimpleSerial.KangarooSerial;
import Hardware.Kanagaroo.KangarooSimpleSerial.KangarooSimpleChannel;

import ControlThreads.LineFollowControlThread;
import ControlThreads.MotherValuesControlThread;
import ObstacleDetection.ObstacleDetectionController;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.library.pigpio.PiGpio;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalInputProvider;
import com.pi4j.plugin.pigpio.provider.gpio.digital.PiGpioDigitalOutputProvider;
import com.pi4j.plugin.pigpio.provider.pwm.PiGpioPwmProvider;
import com.pi4j.plugin.pigpio.provider.serial.PiGpioSerialProvider;
import com.pi4j.plugin.pigpio.provider.spi.PiGpioSpiProvider;
import com.pi4j.plugin.raspberrypi.platform.RaspberryPiPlatform;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package PACKAGE_NAME
 * @date 12/10/2017
 */
@Log4j2
public class Main {

    private static AtomicBoolean isRunning;
    private static IZZYMoveLineFollow izzyMove;
    private static AtomicBoolean dangerApproaching;
    private static IZZYOSCReceiverLineFollow IZZYOSCReceiverLineFollow;
    private static IZZYOSCSenderLineFollow IZZYOSCSenderLineFollow;
    private static HeartbeatResponder heartBeat;
    private static SensorArray sensorArray;
    private static KangarooSerial kangaroo;
    private static KangarooSimpleChannel D;
    private static KangarooSimpleChannel T;
    private static DriveReadings driveReadings;
    private static Process obstacleDetectionProcess;
    private static Object kangarooSyncLock;

    public static void main(String[] args) throws InterruptedException {
        log.debug("Hello From IZZY!");
        // Create single breaking point to halt all processing
        isRunning = new AtomicBoolean(true); // set to false stops all loops and ends program

        // Setup Heartbeat
        try {
            heartBeat = new HeartbeatResponder(isRunning);
            Thread heartBeatLoop = new Thread(heartBeat);
            heartBeatLoop.start();
            while(!heartBeat.isHeartbeating()) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("Unsafe Operation. Could not setup heartbeat.");
            e.printStackTrace();
            return;
        }

        //start obstacle detection code at program start
        // TODO: start python program
//        ProcessBuilder pb = new ProcessBuilder("./izzy-obstacle.sh").inheritIO();
//        pb.directory(new File("/home/pi"));
//        try {
//            obstacleDetectionProcess = pb.start();
//        } catch (Exception e) {
//            heartBeat.setErrorMessage("Invalid Operation. Could not start obstacle detection.");
//            heartBeat.setMessageType(MessageType.SETUP_ERROR);
//            return;
//        }

        log.info("1");

        Context context = null;
        ADS1115 ads1115 = null;

        try {
            // create ADS1115 resource
            context = Pi4J.newAutoContext();
            log.info("ADS1115Test started ...");
            ads1115 = ADS1115Builder.get().context(context).build();
            for (int i = 0; i < 10; i++) {
                double aIn0 = ads1115.getAIn0();
                double aIn1 = ads1115.getAIn1();
                double aIn2 = ads1115.getAIn2();
                double aIn3 = ads1115.getAIn3();
                log.info("[{}] Voltages: a0={} V, a1={} V, a2={} V, a3={} V",
                        i, String.format("%.3f", aIn0), String.format("%.3f", aIn1), String.format("%.3f", aIn2), String.format("%.3f", aIn3));
                Thread.sleep(500);
            }
            log.info("ADS1115Test done.");
        } catch (Exception e) {
            log.error("ADS115 Unable to Initialize");
            log.error(e.getLocalizedMessage());
            heartBeat.setErrorMessage("Invalid Operation. Could not setup GPIO.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }

//        final ButtonPanel buttonPanel = new ButtonPanel(gpio);
        log.info("2");
        System.exit(100);

        //Initialize sensors and map sensors to GPIO pins
        LineSensor sensor1 = new LineSensor(14950, 2, "DistanceSensor-A2", ads1115); // Left
        LineSensor sensor2 = new LineSensor(14950, 0, "DistanceSensor-A0", ads1115); // Right

        //Create array of mapped sensors
        sensorArray = new SensorArray(88, 13, 17);
        sensorArray.addSensor(sensor1); // Left
        sensorArray.addSensor(sensor2); // Right

        PiGpio piGpio = PiGpio.newNativeInstance();
        Context pi4j = Pi4J.newContextBuilder()
                .noAutoDetect()
                .add(new RaspberryPiPlatform() {
                    @Override
                    protected String[] getProviders() {
                        return new String[]{};
                    }
                })
                .add(PiGpioDigitalInputProvider.newInstance(piGpio),
                        PiGpioDigitalOutputProvider.newInstance(piGpio),
                        PiGpioPwmProvider.newInstance(piGpio),
                        PiGpioSerialProvider.newInstance(piGpio),
                        PiGpioSpiProvider.newInstance(piGpio),
                        LinuxFsI2CProvider.newInstance()
                )
                .build();

        //Connect to IZZY's motion controller and establish channels
        kangaroo = new KangarooSerial(pi4j);
        kangaroo.open(); // opens a serial connection with motion controller
        D = new KangarooSimpleChannel(kangaroo, 'D'); // drive channel. forward and backward speed
        T = new KangarooSimpleChannel(kangaroo, 'T'); // turn channel. rotation angle
        kangarooSyncLock = new Object();

        // Create boolean to detect if danger is approaching
        dangerApproaching = new AtomicBoolean(false);

        //Create drive sensor tracker
        driveReadings = new DriveReadings(D, T, kangarooSyncLock, isRunning);
        kangaroo.addListener(driveReadings);

        //Create Movement controller to control mini IZZY's movement
        izzyMove = new IZZYMoveLineFollow(D, T, sensorArray, 67.3, 124.5, 20, 100,
                dangerApproaching, kangarooSyncLock, driveReadings);

        try {
            //Create OSC communication object to listen for  commands
            IZZYOSCReceiverLineFollow = new IZZYOSCReceiverLineFollow(isRunning, izzyMove);
        } catch (Exception e) {
            heartBeat.setErrorMessage("Invalid Operation. Could not create Mother Receiver OSC object.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }

        try {
            // Creates Mother OSC sender controller to manage the data values sent to mother
            IZZYOSCSenderLineFollow = new IZZYOSCSenderLineFollow(izzyMove, driveReadings, heartBeat);
        } catch (Exception e) {
            heartBeat.setErrorMessage("Invalid Operation. Could not create Mother Sender OSC object.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }

        // Create thread to handle mother value updates (sending messages to mother)
        Thread motherUpdateLoop = new Thread(new MotherValuesControlThread(isRunning, IZZYOSCSenderLineFollow,
                heartBeat));

        // Creates thread to handle line follow control
        Thread lineFollowLoop = new Thread(new LineFollowControlThread(isRunning, izzyMove, heartBeat));

        // Start listening for mother commands
        IZZYOSCReceiverLineFollow.startListening();


        ObstacleDetectionController obstacleDetectionController;
        try {
            obstacleDetectionController = new ObstacleDetectionController(isRunning, izzyMove, dangerApproaching);
        } catch (Exception e) {
            heartBeat.setErrorMessage("Invalid Operation. Could not create Obstacle Detection object.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }
        Thread obstacleDetectionLoop = new Thread(obstacleDetectionController);

        log.info("Starting");
        //Start loops
        lineFollowLoop.start();
        motherUpdateLoop.start();
        obstacleDetectionLoop.start();

        try {
            //When loops exit, program stops
            lineFollowLoop.join();
            motherUpdateLoop.join();
            obstacleDetectionLoop.join();
            obstacleDetectionProcess.destroy();
            kangaroo.close();
        } catch (Exception e) {
            heartBeat.setErrorMessage("Critical Error. Could not close control threads. Power down IZZY.");
            heartBeat.setMessageType(MessageType.BROKEN);
            return;
        }

        //Closes the receiver port to free up for next use
        IZZYOSCReceiverLineFollow.stopListening();

        //Disconnects listeners from GPIO board
        try {
            ads1115.close();
        } catch (Exception e) {
            log.error("unable to close ads115");
        }
    }
}
