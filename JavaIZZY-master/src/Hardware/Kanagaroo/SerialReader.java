package Hardware.Kanagaroo;

import com.pi4j.io.serial.Serial;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Log4j2
public class SerialReader implements Runnable {

    private final Serial serial;
    private final ArrayList<SerialDataEventListener> listeners;

    private boolean continueReading = true;

    public SerialReader(Serial serial) {
        this.serial = serial;
        this.listeners = new ArrayList<>();
    }

    public void stopReading() {
        continueReading = false;
    }

    public void addListener(SerialDataEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void run() {
        // We use a buffered reader to handle the data received from the serial port
        BufferedReader br = new BufferedReader(new InputStreamReader(serial.getInputStream()));

        try {
            // Data from the GPS is received in lines
            StringBuilder line = new StringBuilder();

            // Read data until the flag is false
            while (continueReading) {
                // First we need to check if there is data available to read.
                // The read() command for pi-gpio-serial is a NON-BLOCKING call, in contrast to typical java input streams.
                int available = serial.available();
                if (available > 0) {
                    for (int i = 0; i < available; i++) {
                        byte b = (byte) br.read();
                        if (b < 32) {
                            // All non-string bytes are handled as line breaks
                            if (line.length() > 0) {
                                // Here we should add code to parse the data to a GPS data object
                                for (SerialDataEventListener listener : listeners) {
                                    log.debug("Received: " + line);
                                    listener.dataReceived(line.toString());
                                }
                                line = new StringBuilder();
                            }
                        } else {
                            line.append((char) b);
                        }
                    }
                } else {
                    Thread.sleep(10);
                }
            }
        } catch (Exception e) {
            log.error("Error reading data from serial: " + e.getMessage());
            log.error(e.getStackTrace());
        }
    }
}