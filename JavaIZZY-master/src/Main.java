//import Kanagaroo.KangarooChannel;
//import Kanagaroo.KangarooConstants;
//import Kanagaroo.KangarooSerial;
import ControlPanel.ButtonPanel;
import KangarooSimpleSerial.KangarooSimpleChannel;
import LineFollowing.LineFollower;
import LineFollowing.LineSensor;
import LineFollowing.SensorArray;
import com.illposed.osc.*;
import java.io.IOException;
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1115Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.net.SocketException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import comm.HeartBeat;
import util.ControlModeFlag;
import Devices.IZZY;
import util.IZZYStatus;

import static comm.PortEnumerations.*;
import static util.ControlModeFlag.*;
import static util.Direction.*;
import static util.OSCAddresses.*;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package PACKAGE_NAME
 * @date 12/10/2017
 */
public class Main {

    private static GpioController gpio = GpioFactory.getInstance();
    private static ADS1115GpioProvider ads1115One = null;
    private static EnumSet<ControlModeFlag> controlModes = EnumSet.noneOf(ControlModeFlag.class);
    private static LineFollower lineFollower = new LineFollower();
    private static OSCPortIn receiver = null;
    private static OSCListener listener = null;
    private static boolean isHeartBeating = false;
    private static boolean isRunning = false;
    private static IZZY izzy = new IZZY();

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws InterruptedException, UnsupportedBusNumberException, IOException {

        isRunning = true;

        /*
        *   Will need some kind of setup for IZZY (including an HMI of some kind) for IP addressing, port setup,
        *   etc.
         */

        izzy.setName("mini-IZZY");
        izzy.setHeartbeatInterval(250); // 100ms interval between heartbeats.
        System.out.println("Hello from IZZY!");

        /*
        *   Listen for heartbeat signal from Mother that triggers a go/no-go state.
         */

        HeartBeat heartbeatListener = new HeartBeat(izzy);
        heartbeatListener.setListener(new HeartBeat.HeartBeatListener() {
            @Override
            public void onHeartBeatReceived() {
                isHeartBeating = true;
            }

            @Override
            public void onIntervalTimeOut() {
                isHeartBeating = false;

            }
        });

        executor.submit(heartbeatListener);
        System.out.println();
        System.out.println("Listening for heartbeat.");

        setupPiGPIO();

        IZZYController izzyController = setupKangarooCommunication(izzy);

        setControlModes();

        startOSCMessaging(izzyController);

        setControlModeListeners();

        System.out.println("Current drive speed: " + izzyController.getCurrentSpeed());
        izzyController.D.S(100);
        izzyController.D.P(100);
        System.out.println("Current drive speed: " + izzyController.getCurrentSpeed());

        while(isRunning) {

            while (isHeartBeating) {
                //System.out.println("Heart is beating.");
                // TODO: I have to figure out how to a) have the program loop;
                // while listening for incoming messages from mother and the heartbeat
                // and then do things when the messages arrive.


            }
            // Shutdown and lock out
            //System.out.println("Lock down.");
            gpio.shutdown();
            stopKangarooCommunication(izzy);
            controlModes.clear();
//      stopOSCMesssaging();
        }

    }

    /*
    *   Set up simple serial communication with the Kangaroo, and return an Izzy Control object.
     */

    private static IZZYController setupKangarooCommunication(IZZY izzy) {
        IZZYController izzyController = new IZZYController();
        izzy.getKangarooSerial().open();
        KangarooSimpleChannel D = new KangarooSimpleChannel(izzy.getKangarooSerial(), 'D');
        KangarooSimpleChannel T = new KangarooSimpleChannel(izzy.getKangarooSerial(), 'T');
        izzyController.setChannels(D,T);
        izzyController.setup(3,9,42);
        return izzyController;
    }

    /*
    *   Close serial connection with Kangaroo.
     */

    private static void stopKangarooCommunication(IZZY izzy) {
        izzy.getKangarooSerial().close();
    }

    /*
    *   Create objects for GPIO access, including an ADS1115 provider for A->D conversion
     */

    private static void setupPiGPIO() throws IOException, UnsupportedBusNumberException {

        // create custom ADS1115 GPIO provider
        ads1115One = new ADS1115GpioProvider(I2CBus.BUS_1, ADS1115GpioProvider.ADS1115_ADDRESS_0x49);

        ads1115One.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1115Pin.ALL);

        /*
            The listener below should somehow end up in the sensor array, to be triggered there. How?
         */
        ads1115One.setEventThreshold(150, ADS1115Pin.ALL);
        ads1115One.setMonitorInterval(100);
    }


    /*
    *   Read model control buttons and set mode flags
     */

    private static void setControlModes() {
        ButtonPanel buttonPanel = new ButtonPanel(gpio);
        if(buttonPanel.getMotherModeState()!=null) controlModes.add(buttonPanel.getMotherModeState());
        if(buttonPanel.getLineFollowingModeState()!=null) controlModes.add(buttonPanel.getLineFollowingModeState());
        if(buttonPanel.getObstacleDetectionModeState()!=null) controlModes.add(buttonPanel.getObstacleDetectionModeState());
        if(buttonPanel.getRFIDTrackingModeState()!=null) controlModes.add(buttonPanel.getRFIDTrackingModeState());
    }

    /*
    *   Add and remove listeners as appropriate for various control modes.
     */

    private static void setControlModeListeners() throws InterruptedException {
        /*
        *   Need to set precedence; can I be in both "Line following" and "Mother" modes? What takes precedence in this situation?
        *   I think, for now, we can't be in both; Mother mode will always override other modes.
         */

        if (controlModes.contains(LINE_FOLLOWING) && !controlModes.contains(MOTHER_MODE)) {
            System.out.println("Line Following");
            setupLineFollowing();
            receiver.addListener(FOLLOW_LINE_FORWARD.valueOf(), listener);
            receiver.removeListener(SIMPLE_MOVE.valueOf(), listener);

            /*
            *   Note: Line following requires being "on" a line at initiation of line following mode. IZZY cannot
            *   "find" a line to follow safely.
             */
        }

        if (controlModes.contains(MOTHER_MODE)) {
            System.out.println("Mother Mode");
            receiver.addListener(SIMPLE_MOVE.valueOf(), listener);
            receiver.removeListener(FOLLOW_LINE.valueOf(), listener);
        }

        if (controlModes.contains(OBSTACLE_DETECTION) && !controlModes.contains(MOTHER_MODE)) {
            System.out.println("Obstacle Detection");
        }

        //System.out.println("Freewheeling mode");
        receiver.addListener(SIMPLE_MOVE.valueOf(), listener);
        receiver.removeListener(FOLLOW_LINE.valueOf(), listener);

    }

    /*
    *   Set up the line following sensors and routines.
     */

    private static void setupLineFollowing() throws InterruptedException {
        /*
        *   An event listener for changes on analog pins. We may want to listen for value changes as opposed to
        *   looping and checking. Certainly that would save processing cycles.
        *
        *   We'll also want to consider whether everything in here wants to go in its own thread, so as not to block
        *   other commands/processes.
         */

        /*
        GpioPinListenerAnalog analogListener = new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
                double value = event.getValue();
                double percent = ((value * 100) / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);
                double voltage = ads1115One.getProgrammableGainAmplifier(event.getPin()).getVoltage() * (percent / 100);
            }
        };*/

        /*
        *   Create sensors
         */

        LineSensor sensor1 = new LineSensor();
        LineSensor sensor2 = new LineSensor();
        LineSensor sensor3 = new LineSensor();

        /*
        *   Set physical offset of sensors from centerline
         */
        sensor1.setDistance(-1);
        sensor2.setDistance(0);
        sensor3.setDistance(1);

        /*
        *   Set the analog input put of the ADS1115 for each sensor
         */
        sensor1.setAnalogInput(gpio.provisionAnalogInputPin(ads1115One, ADS1115Pin.INPUT_A0, "DistanceSensor-A0"));
        sensor2.setAnalogInput(gpio.provisionAnalogInputPin(ads1115One, ADS1115Pin.INPUT_A1, "DistanceSensor-A1"));
        sensor3.setAnalogInput(gpio.provisionAnalogInputPin(ads1115One, ADS1115Pin.INPUT_A2, "DistanceSensor-A2"));

        /*
        *   Populate a sensor array with sensors
         */
        SensorArray sensorArray = new SensorArray();
        sensorArray.addSensor(sensor1);
        sensorArray.addSensor(sensor2);
        sensorArray.addSensor(sensor3);

        /*
        *   Every 100 ms, for 100 seconds, read the sensors and evaluate the PID algorithm for the sensor array
         */
        int i = 0;
        while(i < 1000) {
            sensorArray.pidValue();
            System.out.println(sensorArray.pidValue());
            Thread.sleep(100);
            i++;
        }
    }

    /*
    *   Create a new OSC receiver port and listener for incoming messages.
     */

    private static void startOSCMessaging(IZZYController izzyController) throws SocketException {
        receiver = new OSCPortIn(OSC_RECEIVE_PORT.getValue());

        listener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseOSC(motherMessage, izzyController);
        };

        receiver.startListening();
    }

    /*
    *   Stop all OSC listeners.
     */

    private static void stopOSCMesssaging() {
        receiver.stopListening();
    }

    /*
    *   Parse incoming OSC messages.
     */
    private static void parseOSC(OSCMessage msg, IZZYController izzyController){
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals(SIMPLE_MOVE.valueOf())) {
            System.out.println("calculating move");
            //izzyController.izzySimpleMove((int) msgArgs.get(0), (int) msgArgs.get(1), (int) msgArgs.get(2), 0);
            izzyController.izzyMove((int) msgArgs.get(0));
        }

        if(msg.getAddress().equals(FOLLOW_LINE_FORWARD.valueOf())) {
            izzy.setStatus(IZZYStatus.MOVING);
            lineFollower.setDirection(FORWARD);
            lineFollower.setVelocity((int)msgArgs.get(0));
            //lineFollower.setTarget((int)msgArgs.get(1));
            /*
            *   Trigger the PID loop here.
            *   Respond to PID loop output appropriately by determining a turn parameter.
             */
        }

        if(msg.getAddress().equals(FOLLOW_LINE_BACKWARD.valueOf())) {
            izzy.setStatus(IZZYStatus.MOVING);
            lineFollower.setDirection(REVERSE);
            lineFollower.setVelocity((int)msgArgs.get(0));
            /*
            *   Trigger the PID loop here.
            *   Respond to PID loop output appropriately by determining a turn parameter.
             */
        }
        if(msg.getAddress().equals(FOLLOW_LINE_STOP.valueOf())) {
            izzy.setStatus(IZZYStatus.AVAILABLE);
            lineFollower.setDirection(FORWARD);
            lineFollower.setVelocity(0);
        }
            /*if((int)msgArgs.get(0) == 0){
                IZZYPos.T.P((int) msgArgs.get(1));
            }
            else{
                IZZYPos.D.P((int) msgArgs.get(0));
            }*/
        //IZZYPos.T.P((int) msgArgs.get(1));
        //IZZYPos.D.getP();
    }
}
