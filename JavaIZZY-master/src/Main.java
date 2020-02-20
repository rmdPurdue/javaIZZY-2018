//import Kanagaroo.KangarooChannel;
//import Kanagaroo.KangarooConstants;
//import Kanagaroo.KangarooSerial;
import KangarooSimpleSerial.KangarooSerial;
import KangarooSimpleSerial.KangarooSimpleChannel;
import LineFollowing.LineSensor;
import LineFollowing.SensorArray;
import com.illposed.osc.*;
import java.io.IOException;
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1115Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
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

    public static void main(String[] args) throws java.net.SocketException,InterruptedException, UnsupportedBusNumberException, IOException{

        /* Unused variables
        final double SPEED_OF_SOUND = 0.343; // microns per ns
        final int DANGER_THRESHOLD_INCHES = 6;
        final int WARNING_THRESHOLD_INCHES = 18;
        final double DANGER_THRESHOLD_CM = DANGER_THRESHOLD_INCHES * 2.54;
        final double WARNING_THRESHOLD_CM = WARNING_THRESHOLD_INCHES * 2.54;
        final double DANGER_THRESHOLD_MICRONS = DANGER_THRESHOLD_CM * 10000;
        final double WARNING_THRESHOLD_MICRONS = WARNING_THRESHOLD_CM * 10000;
        */

        System.out.println("Hello from IZZY!");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // create custom ADS1115 GPIO provider
        final ADS1115GpioProvider gpioProvider = new ADS1115GpioProvider(I2CBus.BUS_1, ADS1115GpioProvider.ADS1115_ADDRESS_0x49);
        gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1115Pin.ALL);

        //Initialize sensors and map sensors to GPIO pins
        LineSensor sensor1 = new LineSensor(15000, gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A0, "DistanceSensor-A0"));
        LineSensor sensor2 = new LineSensor(15000, gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A1, "DistanceSensor-A1"));
        LineSensor sensor3 = new LineSensor(15000, gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A2, "DistanceSensor-A2"));

        //Create array of mapped sensors
        SensorArray sensorArray = new SensorArray(50, 0, 0, 19.5);
        sensorArray.addSensor(sensor1);
        sensorArray.addSensor(sensor2);
        sensorArray.addSensor(sensor3);

        //Connect to IZZY's motion controller and establish channels
        KangarooSerial kangaroo = new KangarooSerial();
        kangaroo.open();
        KangarooSimpleChannel D = new KangarooSimpleChannel(kangaroo, 'D');
        KangarooSimpleChannel T = new KangarooSimpleChannel(kangaroo, 'T');

        //Create IZZYPosition to control IZZY's movement
        IZZYPosition IZZYPos = new IZZYPosition(D, T, 3, 7, 512);
        //IZZYPos.izzyMove(2000);
        //IZZYPos.izzyTurn(3); //WHAT UNITS?

        //Create OSC communication objects
        OSCPortIn receiver = new OSCPortIn(9000);
        OSCListener listener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseOSC(motherMessage,IZZYPos);
        };

        //TODO: figure out how to implement OSC parsing (and from what source?) -->
        receiver.addListener("/IZZY/SimpleMove", listener);
        receiver.startListening();

        int i = 0;
        while(i < 1000) {
            sensorArray.readSensors(); // updates the sensor array with current readings
            sensorArray.calculatePID(); // calculates the adjustment needed for movement
                    System.out.println(sensorArray.getPidValue()); //just for testing purposes
            //TODO: Implement movement based on PIDValue (use different drive mode?). Kangaroo documentation helpful!
            Thread.sleep(500); //need to decide on best value to use here
            i++;
        }
    }

    private static void parseOSC(OSCMessage msg, IZZYPosition IZZYPos){
        List<Object> msgArgs = msg.getArguments();
        if (msg.getAddress() == "/IZZY/SimpleMove")
            System.out.println("calculating move");
            IZZYPos.izzySimpleMove((int)msgArgs.get(0),(int)msgArgs.get(1), (int)msgArgs.get(2),0 );
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
