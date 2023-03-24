//package Hardware.Kanagaroo;
//
//import com.pi4j.io.serial.*;
//import com.pi4j.util.CommandArgumentParser;
//import com.pi4j.util.Console;
//import lombok.extern.log4j.Log4j2;
//
//import java.io.IOException;
//import java.util.Date;
//
///**
// * @author Rich Dionne
// * @project JavaIZZY
// * @package Hardware.Kanagaroo
// * @date 12/17/2017
// */
//@Log4j2
//public class SerialTest {
//
//    public static void main(String args[]) throws InterruptedException, IOException {
//        final Console console = new Console();
//        console.title("<-- The Pi4J Project -->", "Serial Communication Example");
//        console.promptForExit();
//        final Serial serial = SerialFactory.createInstance();
//        serial.addListener(new SerialDataEventListener() {
//            @Override
//            public void dataReceived(SerialDataEvent event) {
//                try {
//                    console.println("[HEX DATA]   " + event.getHexByteString());
//                    console.println("[ASCII DATA] " + event.getAsciiString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        try {
//            SerialConfig config = new SerialConfig();
//            config.device("/dev/ttyAMA0")
//                    .baud(Baud._38400)
//                    .dataBits(DataBits._8)
//                    .parity(Parity.NONE)
//                    .stopBits(StopBits._1)
//                    .flowControl(FlowControl.NONE);
//            if (args.length > 0) {
//                config = CommandArgumentParser.getSerialConfig(config, args);
//            }
//            console.box(" Connecting to: " + config.toString(),
//                    " We are sending ASCII data on the serial port every 1 second.",
//                    " Data received on serial port will be displayed below.");
//            serial.open(config);
//            log.debug("Serial Opened!");
//            while (console.isRunning()) {
//                try {
//                    serial.write("CURRENT TIME: " + new Date().toString());
//                    serial.write((byte) 13);
//                    serial.write((byte) 10);
//                    serial.write("Second Line");
//                    serial.write('\r');
//                    serial.write('\n');
//                    serial.write("Third Line");
//                } catch (IllegalStateException ex) {
//                    ex.printStackTrace();
//                }
//                Thread.sleep(1000);
//            }
//        } catch(IOException ex) {
//            console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
//            return;
//        }
//    }
//}
