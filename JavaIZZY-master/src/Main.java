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
        final String portName = "/dev/ttyAMA0";

        final double SPEED_OF_SOUND = 0.343; // microns per ns
        final int DANGER_THRESHOLD_INCHES = 6;
        final int WARNING_THRESHOLD_INCHES = 18;
        final double DANGER_THRESHOLD_CM = DANGER_THRESHOLD_INCHES * 2.54;
        final double WARNING_THRESHOLD_CM = WARNING_THRESHOLD_INCHES * 2.54;
        final double DANGER_THRESHOLD_MICRONS = DANGER_THRESHOLD_CM * 10000;
        final double WARNING_THRESHOLD_MICRONS = WARNING_THRESHOLD_CM * 10000;

        System.out.println("Hello from IZZY!");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // create custom ADS1115 GPIO provider
        final ADS1115GpioProvider gpioProvider = new ADS1115GpioProvider(I2CBus.BUS_1, ADS1115GpioProvider.ADS1115_ADDRESS_0x49);

        gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1115Pin.ALL);


        //    The listener below should somehow end up in the sensor array, to be triggered there. How?

//        gpioProvider.setEventThreshold(150, ADS1115Pin.ALL);
//        gpioProvider.setMonitorInterval(100);

//        GpioPinListenerAnalog analogListener = new GpioPinListenerAnalog() {
//            @Override
//            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
//                double value = event.getValue();
//                double percent = ((value * 100) / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);
//                double voltage = gpioProvider.getProgrammableGainAmplifier(event.getPin()).getVoltage() * (percent / 100);
//            }
//        };

        LineSensor sensor1 = new LineSensor(15000);
        LineSensor sensor2 = new LineSensor(15000);
        LineSensor sensor3 = new LineSensor(15000);

        sensor1.setAnalogInput(gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A0, "DistanceSensor-A0"));
        sensor2.setAnalogInput(gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A1, "DistanceSensor-A1"));
        sensor3.setAnalogInput(gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A2, "DistanceSensor-A2"));

//        sensor1.setGain(-1);
//        sensor2.setGain(0);
//        sensor3.setGain(1);

        SensorArray sensorArray = new SensorArray();
        sensorArray.addSensor(sensor1);
        sensorArray.addSensor(sensor2);
        sensorArray.addSensor(sensor3);

        int i = 0;
//        while(i < 1000) {
/*          double sensor1Value = sensor1.getSensorReading();
            double sensor1Percent = ((sensor1Value * 100) / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);
            double sensor1Voltage = gpioProvider.getProgrammableGainAmplifier(sensor1.getAnalogInput()).getVoltage() * (sensor1Percent / 100);
            double sensor2Value = sensor2.getSensorReading();
            double sensor2Percent = ((sensor2Value * 100) / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);
            double sensor2Voltage = gpioProvider.getProgrammableGainAmplifier(sensor2.getAnalogInput()).getVoltage() * (sensor2Percent / 100);
            double sensor3Value = sensor3.getSensorReading();
            double sensor3Percent = ((sensor3Value * 100) / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);
            double sensor3Voltage = gpioProvider.getProgrammableGainAmplifier(sensor3.getAnalogInput()).getVoltage() * (sensor3Percent / 100);
(
            System.out.println(sensor1.getAnalogInput().getName() + " = " + sensor1Voltage);
            System.out.println(sensor2.getAnalogInput().getName() + " = " + sensor2Voltage);
            System.out.println(sensor3.getAnalogInput().getName() + " = " + sensor3Voltage);
            System.out.println();

            sensorArray.calculatePID();
            System.out.println(sensorArray.getPidValue());
            Thread.sleep(1000);
            i++;
        }
        */

        KangarooSerial kangaroo = new KangarooSerial();
        IZZYPosition IZZYPos = new IZZYPosition();
        kangaroo.open();
        KangarooSimpleChannel D = new KangarooSimpleChannel(kangaroo, 'D');
        KangarooSimpleChannel T = new KangarooSimpleChannel(kangaroo, 'T');
        IZZYPos.setChannels(D,T);
        IZZYPos.setup(3,7,42);
        //IZZYPos.izzyMove(2000);
        //IZZYPos.izzyTurn(3); //WHAT UNITS?
        OSCPortIn receiver = new OSCPortIn(9000);

        OSCListener listener = (time, motherMessage) -> {
            System.out.println("Message received from Mother!");

            parseOSC(motherMessage,IZZYPos);
        };
        receiver.addListener("/IZZY/SimpleMove", listener);
        receiver.startListening();
        //while(true);
        /*D.getP();
        T.getP();
        //One.getP();
        T.P(0);
        D.S(50);
        D.getP();
        D.S(500);
        try {
            Thread.sleep(60000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        D.getP();
        D.S(0);*/
        //T.P(0);
        //D.P(256);
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
