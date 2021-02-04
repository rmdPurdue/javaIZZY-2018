import KangarooSimpleSerial.KangarooSerial;
import KangarooSimpleSerial.KangarooSimpleChannel;
import LineFollowing.*;
import java.io.IOException;

import LineFollowing.ControlThreads.LineFollowControlThread;
import LineFollowing.ControlThreads.MotherValuesControlThread;
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1115Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package PACKAGE_NAME
 * @date 12/10/2017
 */
public class Main {

    private static AtomicInteger speed;
    private static AtomicBoolean moving;
    private static AtomicBoolean running;
    private static PIDCalculations pidCalculations;
    private static IZZYMoveLineFollow izzyMove;
    private static IZZYMotherOSCLineFollow izzyMotherOSCLineFollow;
    private static SensorArray sensorArray;
    private static KangarooSerial kangaroo;
    private static KangarooSimpleChannel D;
    private static KangarooSimpleChannel T;

    public static void main(String[] args) throws InterruptedException, UnsupportedBusNumberException, IOException {

        // set default values for static variables.
        running = new AtomicBoolean(true); // set to false stops all loops and ends program
        moving = new AtomicBoolean(false); // IZZY's current motion state
        speed = new AtomicInteger(0); // IZZY's current speed value

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // create custom ADS1115 GPIO provider
        final ADS1115GpioProvider gpioProvider = new ADS1115GpioProvider(I2CBus.BUS_1,
                ADS1115GpioProvider.ADS1115_ADDRESS_0x49);
        gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1115Pin.ALL);

        //Initialize sensors and map sensors to GPIO pins
        LineSensor sensor1 = new LineSensor(17200, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A0, "DistanceSensor-A0")); // Left
        LineSensor sensor2 = new LineSensor(17200, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A1, "DistanceSensor-A1")); // Center
        LineSensor sensor3 = new LineSensor(17200, gpio.provisionAnalogInputPin(gpioProvider,
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

        //Create IZZYMovement.IZZYMove to control mini IZZY's movement
        izzyMove = new IZZYMoveLineFollow(D, T, 31.75, 69.5, 16, 120);

        //Create LineFollowing.PIDControl to monitor sensors and automate movement
        pidCalculations = new PIDCalculations(1, 1, 1, sensorArray);

        //Create OSC communication object to listen for mother
        izzyMotherOSCLineFollow = new IZZYMotherOSCLineFollow(moving, speed, pidCalculations, running, D, T);

        // Start listening for mother commands
        izzyMotherOSCLineFollow.startListening();

        // Creates thread to handle line follow control
        Thread lineFollowLoop = new Thread(new LineFollowControlThread(running, pidCalculations, sensorArray, moving, izzyMove,
                                            speed));

        // Create thread to handle mother value updates (sending messages to mother)
        Thread motherUpdateLoop = new Thread(new MotherValuesControlThread(running, speed, pidCalculations, moving, sensorArray));

        //Start loops
        lineFollowLoop.start();
        motherUpdateLoop.start();

        //When loops exit, program stops
        lineFollowLoop.join();
        motherUpdateLoop.join();

        //Closes the receiver port to free up for next use
        izzyMotherOSCLineFollow.close();

    }

//    private static void parseOSC(OSCMessage msg, IZZYMovement.IZZYPosition IZZYPos){
//        List<Object> msgArgs = msg.getArguments();
//        if (msg.getAddress() == "/IZZY/SimpleMove") {
//            System.out.println("calculating move");
//            IZZYPos.izzySimpleMove((int) msgArgs.get(0), (int) msgArgs.get(1), (int) msgArgs.get(2), 0);
//            /*if((int)msgArgs.get(0) == 0){
//                IZZYPos.T.P((int) msgArgs.get(1));
//            }
//            else{
//                IZZYPos.D.P((int) msgArgs.get(0));
//            }*/
//            //IZZYPos.T.P((int) msgArgs.get(1));
//            //IZZYPos.D.getP();
//        }
//    }
}
