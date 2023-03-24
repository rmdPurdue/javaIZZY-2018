package ObstacleDetection;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author eholl
 */
@Log4j2
public class RPLidarServer {

    private final DatagramSocket s;
    private Socket incoming;

    public RPLidarServer(int portNum) throws IOException {
        this.s = new DatagramSocket(portNum); //the server socket
        this.s.setSoTimeout(2000);
        this.incoming = null;
    }

    /**
     * Closes all ports used for obstacle detection communication
     */
    public void stopServer() {

        try {
            if (incoming != null) {
                incoming.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Accepts an array of 361 length with distance measurements at each angle. The 361 value is
     * a set of flagged zones. There are 8 zones clockwise starting at center. The most significant bits
     * indicate an object in the respective zone within 400-200mm. The least significant bits indicate
     * an object in the respective zone within 0-200mm.
     *
     * @return Array of size 361 with 0-360 representing distances. 361 representing flagged zones
     */
    public short[] receiveRawDataMessage() {
        try {
            byte[] buf = new byte[724];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            s.receive(packet);
            short[] shorts = new short[buf.length/2];
// to turn bytes to shorts as either big endian or little endian.
            ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            return shorts;
        } catch (SocketException socketClosedError) {
            //ignore
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
