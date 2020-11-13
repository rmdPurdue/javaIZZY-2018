import KangarooSimpleSerial.KangarooSerial;
import KangarooSimpleSerial.KangarooSimpleChannel;
import LineFollowing.IZZYMove;
import LineFollowing.LineSensor;
import LineFollowing.PIDControl;
import LineFollowing.SensorArray;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import java.io.IOException;

import com.illposed.osc.OSCPortOut;
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1115Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.net.InetAddress;
import java.util.List;
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
    private static PIDControl pidControl;
    private static IZZYMove izzyMove;
    private static SensorArray sensorArray;
    private static KangarooSerial kangaroo;
    private static KangarooSimpleChannel D;
    private static KangarooSimpleChannel T;

    public static void main(String[] args) throws java.net.SocketException, InterruptedException, UnsupportedBusNumberException, IOException {

        System.out.println("Hello from IZZY!");
        running = new AtomicBoolean(true);
        moving = new AtomicBoolean(false);
        speed = new AtomicInteger(0);

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // create custom ADS1115 GPIO provider
        final ADS1115GpioProvider gpioProvider = new ADS1115GpioProvider(I2CBus.BUS_1,
                ADS1115GpioProvider.ADS1115_ADDRESS_0x49);
        gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1115Pin.ALL);

        //Initialize sensors and map sensors to GPIO pins
        LineSensor sensor1 = new LineSensor(15000, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A0, "DistanceSensor-A0"));
        LineSensor sensor2 = new LineSensor(15000, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A1, "DistanceSensor-A1"));
        LineSensor sensor3 = new LineSensor(15000, gpio.provisionAnalogInputPin(gpioProvider,
                ADS1115Pin.INPUT_A2, "DistanceSensor-A2"));

        //Create array of mapped sensors
        sensorArray = new SensorArray(101.6, 45, 30);
        sensorArray.addSensor(sensor1);
        sensorArray.addSensor(sensor2);
        sensorArray.addSensor(sensor3);

        //Connect to IZZY's motion controller and establish channels
        kangaroo = new KangarooSerial();
        kangaroo.open();
        D = new KangarooSimpleChannel(kangaroo, 'D');
        T = new KangarooSimpleChannel(kangaroo, 'T');

        //Create LineFollowing.IZZYMove to control mini IZZY's movement
        izzyMove = new IZZYMove(D, T, 31.75, 69.5, 16, 120);

        //Start LineFollowing PID Loop
        pidControl = new PIDControl(1, 1, 1, sensorArray);

        //Create OSC communication objects
        OSCPortIn receiver = new OSCPortIn(9000);

        OSCListener followLineStateListener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseFollowLineStateOSC(motherMessage);
        };
        OSCListener followLineSpeedListener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseFollowLineSpeedOSC(motherMessage);
        };
        OSCListener followLineTuneListener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseFollowLineTuneOSC(motherMessage);
        };
        OSCListener eStopListener = (time, motherMessage) -> {
            System.out.println("ESTOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            eStopOSC(motherMessage);
        };
        receiver.addListener("/IZZY/FollowLineState", followLineStateListener);
        receiver.addListener("/IZZY/FollowLineSpeed", followLineSpeedListener);
        receiver.addListener("/IZZY/FollowLineTune", followLineTuneListener);
        receiver.addListener("/IZZY/eStop", eStopListener);

        receiver.startListening();

        Thread looping = new Thread(new LoopingLineFollowControl());
        Thread updating = new Thread(new UpdateMotherValues());

        looping.start();
        updating.start();

        looping.join();
        updating.join();


        receiver.close();

//        int i = 0;
//        while(i < 1000) {
//            try {
//                pidControl.adjustError(sensorArray.readSensors());
//                pidControl.calculatePID();
//                izzyMove.followLine((int) (pidControl.getErrorAngle() + 0.5), 30);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            i++;
//        }
    }

//    private static void parseOSC(OSCMessage msg, IZZYPosition IZZYPos){
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

    private static void parseFollowLineStateOSC(OSCMessage msg) {
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

    private static void parseFollowLineSpeedOSC(OSCMessage msg) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/FollowLineSpeed")) {
            speed.set((int) msgArgs.get(0));
        }
    }

    private static void parseFollowLineTuneOSC(OSCMessage msg) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/FollowLineTune")) {
            pidControl.setKp((double) msgArgs.get(0));
            pidControl.setKi((double) msgArgs.get(1));
            pidControl.setKd((double) msgArgs.get(2));
        }
    }

    private static void eStopOSC(OSCMessage msg) {
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

    private static class LoopingLineFollowControl implements Runnable {
        @Override
        public void run() {
            try {
                while (running.get()) {
                    try {
                        pidControl.adjustError(sensorArray.readSensors());
                        if (moving.get()) {
                            pidControl.calculatePID();
                            izzyMove.followLine((int) (pidControl.getErrorAngle() + 0.5), speed.get());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    if (!moving.get()) {
                        izzyMove.followLine((int) (pidControl.getErrorAngle() + 0.5), 0);
                    }

                    Thread.sleep(250);
                }
            } catch (Exception e) {
                System.out.println("The control interface has stopped looping");
                e.printStackTrace();
            }
        }
    }

    private static class UpdateMotherValues implements Runnable {

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
                        outgoingMessage.addArgument(pidControl.getPidValue());
                        outgoingMessage.addArgument(pidControl.getErrorAngle());
                        outgoingMessage.addArgument(pidControl.getKp());
                        outgoingMessage.addArgument(pidControl.getKi());
                        outgoingMessage.addArgument(pidControl.getKd());
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

}
