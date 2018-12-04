//import Kanagaroo.KangarooChannel;
//import Kanagaroo.KangarooConstants;
//import Kanagaroo.KangarooSerial;

import KangarooSimpleSerial.KangarooSerial;
import KangarooSimpleSerial.KangarooSimpleChannel;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1115Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package PACKAGE_NAME
 * @date 12/10/2017
 */
public class ADCTester {


    public static void main(String[] args) throws java.net.SocketException,InterruptedException, UnsupportedBusNumberException, IOException{
        final String portName = "/dev/ttyAMA0";


        testADC();
    }


        public static void testADC() throws InterruptedException, UnsupportedBusNumberException, IOException {



            System.out.println("<--Pi4J--> ADS1115 testing.");

            final GpioController gpio = GpioFactory.getInstance();

            final ADS1115GpioProvider gpioProvider = new ADS1115GpioProvider(I2CBus.BUS_1, ADS1115GpioProvider.ADS1115_ADDRESS_0x49);

            final GpioPinAnalogInput distanceSensorPin = gpio.provisionAnalogInputPin(gpioProvider, ADS1115Pin.INPUT_A0, "DistanceSensor-A0");

            gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_4_096V, ADS1115Pin.ALL);





            // Define a threshold value for each pin for analog value change events to be raised.

            // It is important to set this threshold high enough so that you don't overwhelm your program with change events for insignificant changes

            gpioProvider.setEventThreshold(150, ADS1115Pin.ALL);





            // Define the monitoring thread refresh interval (in milliseconds).

            // This governs the rate at which the monitoring thread will read input values from the ADC chip

            // (a value less than 50 ms is not permitted)

            GpioPinListenerAnalog pinListener = new GpioPinListenerAnalog() {

                public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
                }
            };

            distanceSensorPin.addListener(pinListener);
            int i = 0;
            double Raw;
            double voltage;
            double percent;
            gpioProvider.setMonitorInterval(100);
            while(i < 1000) {
                Raw = distanceSensorPin.getValue();
                percent = (Raw / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);//get percent of max value
                voltage = gpioProvider.getProgrammableGainAmplifier(distanceSensorPin).getVoltage() * percent;
                System.out.println(voltage);
                TimeUnit.SECONDS.sleep(2);
                i++;
            }



            // stop all GPIO activity/threads by shutting down the GPIO controller

            // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)

            gpio.shutdown();



            System.out.println("Exiting ADS1115DistanceSensorExample");

        }
}
