package Hardware.Kanagaroo.KangarooSimpleSerial;

import Hardware.Kanagaroo.SerialDataEventListener;
import Hardware.Kanagaroo.SerialReader;
import com.pi4j.context.Context;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.StopBits;
import lombok.extern.log4j.Log4j2;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
//import javax.comm.Commport;


import java.util.Arrays;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Hardware.Kanagaroo.KangarooSimpleSerial
 * @date 12/18/2017
 */
@Log4j2
public class KangarooSerial implements AutoCloseable {

    private SerialConfig port;
    private Serial serial;
    byte[] buffer;
    boolean open;
    SerialReader serialReader;
    Thread serialReaderThread;

    public KangarooSerial(Context pi4j) {
        this.serial = pi4j.create(Serial.newConfigBuilder(pi4j)
                .use_9600_N81()
                .dataBits_8()
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
                .id("my-serial")
                .device("/dev/ttyS0")
                .provider("pi-gpio-serial")
                .build());
        this.serialReader = new SerialReader(serial);
        this.serialReaderThread = new Thread(serialReader);
    }

    public void open() {
        serial.open();
        serialReaderThread.start();
        while (!serial.isOpen()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        this.open = true;
        this.buffer = new byte[256];
    }

    public void addListener(SerialDataEventListener listener) {
        serialReader.addListener(listener);
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() {
        if(!open) {
            return;
        }

        try {
            this.serial.close();
            this.serialReader.stopReading();
            this.serialReaderThread.join();
            this.open = false;
        } catch (Exception e) { }
    }

    public void write(KangarooSimpleChannel channel, String command) {
        if(!open) {
            log.error("Error: port not open.");
            return;
        }
        String commandToSend = channel.getName() + "," + command;
        byte[] data = commandToSend.getBytes();
//        for(byte datum : data) {
//            System.out.println(datum);
//        }
        log.debug("Writing message: " + commandToSend + " as " + Arrays.toString(data));
        serial.write(data);
        log.debug("Complete");
        //else{
        //System.out.println("system has started\n");
        //}
    }
}
