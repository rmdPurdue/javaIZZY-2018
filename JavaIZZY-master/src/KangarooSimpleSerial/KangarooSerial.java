package KangarooSimpleSerial;

import com.pi4j.io.serial.*;
//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
//import javax.comm.Commport;


import java.io.IOException;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package KangarooSimpleSerial
 * @date 12/18/2017
 */
public class KangarooSerial implements AutoCloseable {

    private SerialConfig port;
    private Serial serial;
    byte[] buffer;
    boolean open;

    public KangarooSerial() {
        this.serial = SerialFactory.createInstance();

//        serial.addListener(new SerialDataEventListener() {
//            @Override
//            public void dataReceived(SerialDataEvent event) {
//                try {
//                    System.out.println("[HEX DATA]" + event.getHexByteString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        this.port = new SerialConfig();
        port.device("/dev/ttyAMA0")
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);
    }

    public void open() {
//        System.out.println(this.port.toString());
        open("/dev/ttyAMA0");
    }

    private void open(String port) {
        try {
            this.serial.open(this.port.device(port));
            this.open = true;
            this.buffer = new byte[256];
        } catch (IOException e) {
            System.out.println(" ==>> SERIAL SETUP FAILED: " + e.getMessage());
            this.open = false;
        }
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

    public boolean tryReceivePacket() {
        while(true) {
            if(!open) {
                System.out.println("Port not open.");
                return false;
            }
            try {
                byte[] data = this.serial.read();
                System.out.println("Received: ");
                for(byte datum : data) {
                    System.out.print(datum + " ");
                }
                System.out.println("debug ended");
                System.out.print(new String(data));
                System.out.println();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(KangarooSimpleChannel channel, String command) {
        if(!open) {
            return;
        }
        String commandToSend = channel.getName() + "," + command;
        System.out.println(commandToSend);
        byte[] data = commandToSend.getBytes();
        for(byte datum : data) {
            //System.out.println(datum);
        }
        try {
            serial.write(data);
            if(command.toLowerCase().contains("get".toLowerCase())) {
                tryReceivePacket();
            }
            //else{
                //System.out.println("system has started\n");
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
