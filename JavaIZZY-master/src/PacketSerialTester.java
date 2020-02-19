
import Kanagaroo.KangarooConstants;
import Kanagaroo.KangarooSerial;
import Kanagaroo.KangarooChannel;
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
public class PacketSerialTester {


    public static void main(String[] args) throws java.net.SocketException, InterruptedException, UnsupportedBusNumberException, IOException {
        final String portName = "/dev/ttyAMA0";


        testPC();
    }

    public static void testPC() throws IOException, InterruptedException {
        final String portName = "/dev/ttyAMA0";

        final double SPEED_OF_SOUND = 0.343; // microns per ns
        final int DANGER_THRESHOLD_INCHES = 6;
        final int WARNING_THRESHOLD_INCHES = 18;
        final double DANGER_THRESHOLD_CM = DANGER_THRESHOLD_INCHES * 2.54;
        final double WARNING_THRESHOLD_CM = WARNING_THRESHOLD_INCHES * 2.54;
        final double DANGER_THRESHOLD_MICRONS = DANGER_THRESHOLD_CM * 10000;
        final double WARNING_THRESHOLD_MICRONS = WARNING_THRESHOLD_CM * 10000;

        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_WHITE = "\u001B[37m";
        System.out.println("Hello from IZZY!");

        KangarooSerial testSerial = new KangarooSerial();
        testSerial.open(portName);
        KangarooChannel testChannel = new KangarooChannel(testSerial,'D',KangarooConstants.DEFAULT_ADDRESS);
        testChannel.setStreaming(true);
        testChannel.start();
        System.out.println("IZZYChannel D started!");
        Thread.sleep(1000);
        testChannel.getP();
        Thread.sleep(1000);
        testChannel.P(200);
        while(true);

    }
}


