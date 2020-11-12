import KangarooSimpleSerial.KangarooSerial;
import KangarooSimpleSerial.KangarooSimpleChannel;
import LineFollowing.IZZYMove;
import LineFollowing.LineSensor;
import LineFollowing.PIDControl;
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
import java.util.List;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package PACKAGE_NAME
 * @date 12/10/2017
 */
public class Main {

    private static int speed = 0;
    private static boolean following;

    public static void main(String[] args) throws java.net.SocketException,InterruptedException, UnsupportedBusNumberException, IOException{

        System.out.println("Hello from IZZY!");

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
        SensorArray sensorArray = new SensorArray(101.6, 15, 30);
        sensorArray.addSensor(sensor1);
        sensorArray.addSensor(sensor2);
        sensorArray.addSensor(sensor3);

        //Connect to IZZY's motion controller and establish channels
        KangarooSerial kangaroo = new KangarooSerial();
        kangaroo.open();
        KangarooSimpleChannel D = new KangarooSimpleChannel(kangaroo, 'D');
        KangarooSimpleChannel T = new KangarooSimpleChannel(kangaroo, 'T');

        //Create LineFollowing.IZZYMove to control mini IZZY's movement
        IZZYMove izzyMove = new IZZYMove(D, T, 31.75, 69.5, 16, 120);

        //Start LineFollowing PID Loop
        PIDControl pidControl = new PIDControl(1, 1, 1, sensorArray);

        //Create OSC communication objects
        OSCPortIn receiver = new OSCPortIn(9000);
        OSCListener listener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseOSC(motherMessage, izzyMove, pidControl, sensorArray);
        };

        receiver.addListener("/IZZY/FollowLineState", listener);
        receiver.startListening();



        int i = 0;
        while(i < 1000) {
            try {
                pidControl.adjustError(sensorArray.readSensors());
                pidControl.calculatePID();
                izzyMove.followLine((int) (pidControl.getErrorAngle() + 0.5), 30);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    private static void parseOSC(OSCMessage msg, IZZYPosition IZZYPos){
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress() == "/IZZY/SimpleMove") {
            System.out.println("calculating move");
            IZZYPos.izzySimpleMove((int) msgArgs.get(0), (int) msgArgs.get(1), (int) msgArgs.get(2), 0);
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

    private static void parseOSC(OSCMessage msg, IZZYMove izzyMove, PIDControl pidControl, SensorArray sensorArray) {
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress().equals("/IZZY/FollowLineState")) {
            if (msgArgs.get(0).equals("start")) {
                following = true;
                try {
                    do {
                        pidControl.adjustError(sensorArray.readSensors());
                        pidControl.calculatePID();
                        izzyMove.followLine((int) (pidControl.getErrorAngle() + 0.5), (int) msgArgs.get(1));
                        Thread.sleep(100);
                    } while (following);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if (msgArgs.get(0).equals("stop")) {
                following = false;
            }
        }
    }

    private class FollowLineThread implements Runnable {

        PIDControl pidControl;
        SensorArray sensorArray;
        IZZYMove izzyMove;
        List<Object> msgArgs;

        public FollowLineThread(PIDControl pidControl, SensorArray sensorArray, IZZYMove izzyMove, List<Object> msgArgs) throws Exception {
            this.pidControl = pidControl;
            this.sensorArray = sensorArray;
            this.izzyMove = izzyMove;
            this.msgArgs = msgArgs;
        }

        @Override
        public void run() {
            do {
                try {
                    pidControl.adjustError(sensorArray.readSensors());
                    pidControl.calculatePID();
                    izzyMove.followLine((int) (pidControl.getErrorAngle() + 0.5), (int) msgArgs.get(1));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            } while (true);
        }
    }

}
