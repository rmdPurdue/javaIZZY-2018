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
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1115Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package PACKAGE_NAME
 * @date 12/10/2017
 */
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
    private static Process obstacleDetectionProcess;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello From IZZY!");

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
            System.out.println("Unsafe Operation. Could not setup heartbeat.");
            e.printStackTrace();
            return;
        }

        //start obstacle detection code at program start
        ProcessBuilder pb = new ProcessBuilder("./izzy-obstacle.sh").inheritIO();
        pb.directory(new File("/home/pi"));
        try {
            obstacleDetectionProcess = pb.start();
        } catch (Exception e) {
            heartBeat.setErrorMessage("Invalid Operation. Could not start obstacle detection.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        final ADS1115GpioProvider gpioProvider;

        try {
            // create custom ADS1115 GPIO provider
            gpioProvider = new ADS1115GpioProvider(I2CBus.BUS_1,
                    ADS1115GpioProvider.ADS1115_ADDRESS_0x49);
            gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1115Pin.ALL);
        } catch (Exception e) {
            heartBeat.setErrorMessage("Invalid Operation. Could not setup GPIO.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }

        //Initialize sensors and map sensors to GPIO pins
        LineSensor sensor1 = new LineSensor(14950, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A0, "DistanceSensor-A0")); // Left
        LineSensor sensor2 = new LineSensor(14950, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A1, "DistanceSensor-A1")); // Center
        LineSensor sensor3 = new LineSensor(14950, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A2, "DistanceSensor-A2")); // Right

        //Create array of mapped sensors
        sensorArray = new SensorArray(101.6, 45, 30);
        sensorArray.addSensor(sensor1); // Left
        sensorArray.addSensor(sensor2); // Center
        sensorArray.addSensor(sensor3); // Right

        //Connect to IZZY's motion controller and establish channels
        kangaroo = new KangarooSerial();
        kangaroo.open(); // opens a serial connection with motion controller
        D = new KangarooSimpleChannel(kangaroo, 'D'); // drive channel. forward and backward speed
        T = new KangarooSimpleChannel(kangaroo, 'T'); // turn channel. rotation angle

        // Create boolean to detect if danger is approaching
        dangerApproaching = new AtomicBoolean(false);

        //Create Movement controller to control mini IZZY's movement
        izzyMove = new IZZYMoveLineFollow(D, T, sensorArray, 67.3, 124.5, 20, 100, dangerApproaching);

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
            IZZYOSCSenderLineFollow = new IZZYOSCSenderLineFollow(izzyMove, heartBeat);
        } catch (Exception e) {
            heartBeat.setErrorMessage("Invalid Operation. Could not create Mother Sender OSC object.");
            heartBeat.setMessageType(MessageType.SETUP_ERROR);
            return;
        }

        // Create thread to handle mother value updates (sending messages to mother)
        Thread motherUpdateLoop = new Thread(new MotherValuesControlThread(isRunning, IZZYOSCSenderLineFollow,
                heartBeat));

        // Creates thread to handle line follow control
        Thread lineFollowLoop = new Thread(new LineFollowControlThread(isRunning, izzyMove, heartBeat,
                IZZYOSCSenderLineFollow));

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
        } catch (Exception e) {
            heartBeat.setErrorMessage("Critical Error. Could not close control threads. Power down IZZY.");
            heartBeat.setMessageType(MessageType.BROKEN);
            return;
        }

        //Closes the receiver port to free up for next use
        IZZYOSCReceiverLineFollow.stopListening();

    }
}
