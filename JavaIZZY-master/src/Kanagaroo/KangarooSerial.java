package Kanagaroo;

import com.pi4j.io.serial.*;

import java.io.IOException;
import java.io.InputStream;

//import java.util.stream.Stream;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/11/2017
 */
public class KangarooSerial implements AutoCloseable {

    private SerialConfig port;
    private Serial serial;
    byte[] buffer;
    int bufferOffset;
    int bufferLength;
    boolean open;
    KangarooReplyReceiver receiver;

    public KangarooSerial() throws InterruptedException, IOException {
        this.receiver = new KangarooReplyReceiver();
        this.serial = SerialFactory.createInstance();
        this.port = new SerialConfig();
        port.device("/dev/ttyAMA0")
                .baud(Baud._9600)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);

/*
        serial.addListener(new SerialDataEventListener() {
            @Override
            // this should actually call KangarooReplyReceiver, which should implement addListener.
            public void dataReceived(SerialDataEvent event) {
                try {
                    System.out.println("Got something.");
                    System.out.println("[HEX DATA] " + event.getHexByteString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    public void open() {
        open(this.port.toString());
    }

    public void open(String port) {
        try {
            this.serial.open(this.port.device(port));
            this.open = true;
            this.buffer = new byte[256];
            this.bufferOffset = 0;
            this.bufferLength = 0;
        } catch (IOException e) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + e.getMessage());
            this.open = false;
        }
    }

    public boolean isOpen() {
        return open;
    }

    public SerialConfig getPort() {
        return this.port;
    }

    public Serial getSerial() { return this.serial; }

    @Override
    public void close() throws Exception {
        if(!open) { return; }
        try {
            this.serial.close();
            this.open = false;
        } catch (Exception e) { }
/*
        if(this.serial.read() != null) {
            try {
                this.port.EndRead(this.read);
            } catch (Exception e) { }
        }
*/
    }
/*
    public void open(SerialPort port) {
        if(port == null) { throw new IllegalArgumentException("port"); }
        open(new SerialStream(port));
    }

    public void open(Stream port) {
        if(port == null) {throw new IllegalArgumentException("port"); }

        try { close(); }
        catch (Exception e) {
            e.printStackTrace();
        }

        this.buffer = new byte[256];
        this.bufferOffset = 0;
        this.bufferLength = 0;
        this.port = port;
        this.read = null;
        this.open = true;
    }

    public void open(String portName, int baudRate) {
        if (portName == null) { throw new IllegalArgumentException("portName"); }
        if(baudRate <=0) {throw new IllegalArgumentException("baudRate"); }

        try {close(); }
        catch (Exception e) {
            e.printStackTrace();
        }

        SerialPort port = new SerialPort(portName, baudRate);
        try {
            //port.open();
            //open(port);
        } catch {
            //port.close();
            throw;
        }
    }
*/
    public boolean tryReceivePacket(KangarooTimeout timeout) {
        while(true) {
            if(!open) {
                System.out.println("Port not open.");
                return false;
            }
            try {
                byte[] data = this.serial.read();
                System.out.println("Received: ");
//                for(byte datum : data) {
//                    System.out.println(datum + " : " + Integer.toBinaryString((int)datum));
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte word;
            //System.out.println("Offset: " + this.bufferOffset + " Length: " + this.bufferLength);
            if(this.bufferOffset < this.bufferLength) {
                word = this.buffer[this.bufferOffset++];
                System.out.println("Got this: " + word);
            } else {
//                System.out.println("Buffer Offset is smaller than Buffer Length.");
//                System.out.println("Offset: " + this.bufferOffset + " Length: " + this.bufferLength);
                return false;
            }
            this.receiver.read(word);

//            if(this.receiver.isReady()) { return true; }
            return true;
        }
    }
/*
    boolean tryReceiveByte(KangarooTimeout timeout, byte word) {
        word = 0;

        if(this.bufferOffset < this.bufferLength) {
            word = this.buffer[this.bufferOffset++];
            return true;
        } else {

            if(this.read == null) {
                try {
                    this.read = this.port.BeginRead(this.buffer, 0, this.bufferLength, null, null);
                } catch (Exception e) {
                    try { close(); }
                    catch (Exception ce) {}
                    return false;
                }
            }

            if(this.read.AsyncWaitHandle.WaitOne(timeout.TimeLeft)) {
                int bytesRead;
                try {
                    bytesRead = this.port.Endread(this.read);
                } catch (Exception e) {
                    this.read = null;
                    try { close(); }
                    catch (Exception ce) { }
                    return false;
                }
                this.bufferOffset = 0;
                this.bufferLength = bytesRead;
                this.read = null;
                if(bytesRead <= 0) { return false; }

                word = this.buffer[this.bufferOffset++];
                return true;
            }
            else {
                return false;
            }
        }
    }
    */
}
