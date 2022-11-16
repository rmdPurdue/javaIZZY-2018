package Hardware.Kanagaroo.KangarooSimpleSerial;

import com.pi4j.io.serial.*;
import lombok.extern.log4j.Log4j2;
//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
//import javax.comm.Commport;


import java.io.IOException;

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

    public KangarooSerial() {
        this.serial = SerialFactory.createInstance();
        this.port = new SerialConfig();
        port.device("/dev/ttyS0")
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);
    }

    public void open() {
        open("/dev/ttyS0");
    }

    private void open(String port) {
        try {
            this.serial.open(this.port);
            this.open = true;
            this.buffer = new byte[256];
        } catch (IOException e) {
            log.error(" ==>> SERIAL SETUP FAILED: " + e.getMessage());
            this.open = false;
        }
    }

    public void addListener(SerialDataEventListener listener) {
        serial.addListener(listener);
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
        try {
            serial.write(data);
            //else{
                //System.out.println("system has started\n");
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
