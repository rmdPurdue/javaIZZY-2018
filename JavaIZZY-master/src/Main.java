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
        SensorArray sensorArray = new SensorArray(1, 0, 0, 101.6);
        sensorArray.addSensor(sensor1);
        sensorArray.addSensor(sensor2);
        sensorArray.addSensor(sensor3);

        //Connect to IZZY's motion controller and establish channels
        KangarooSerial kangaroo = new KangarooSerial();
        kangaroo.open();
        KangarooSimpleChannel D = new KangarooSimpleChannel(kangaroo, 'D');
        KangarooSimpleChannel T = new KangarooSimpleChannel(kangaroo, 'T');

        //Create IZZYPosition to control mini IZZY's movement
        IZZYPosition IZZYPos = new IZZYPosition(D, T, 31.75, 69.5, 16, 120);
//        IZZYPos.izzyMove(100); //UNITS = millimeters
//        IZZYPos.izzyTurn(30); //UNITS = degrees

        //Create OSC communication objects
        OSCPortIn receiver = new OSCPortIn(9000);
        OSCListener listener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");
            parseOSC(motherMessage,IZZYPos);
        };

        //TODO: figure out how to implement OSC parsing (and from what source?) -->
        receiver.addListener("/IZZY/SimpleMove", listener);
        receiver.startListening();

        try {
            IZZYPos.followLine(sensorArray, 30);
        } catch (Exception e) {
            e.printStackTrace();
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
