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
import java.util.Arrays;

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

    public short[] receiveMessage() {
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
