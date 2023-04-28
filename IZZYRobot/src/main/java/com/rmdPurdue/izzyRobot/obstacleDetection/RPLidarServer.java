package com.rmdPurdue.izzyRobot.obstacleDetection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author eholl
 */
public class RPLidarServer {
    private static final Logger log = LogManager.getLogger(RPLidarServer.class);
    private static final int BYTES_PER_DOUBLE = 7;
    private static final String LOCAL_IP_ADDRESS = "127.0.0.1";
    private static final int OBSTACLE_REC_PORT = 6911;
    private static final int ACKNOWLEDGED_INDEX = 9;
    private static final int DETECTED_INDEX = 10;

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
    public ObstacleMessage receiveRawDataMessage() {
        try {
            byte[] buf = new byte[16]; // we are receiving an angle heading and a flag indicating an obstacle is detected
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            s.receive(packet);
            byte[] angleBytes = new byte[8];
            System.arraycopy(buf, 0, angleBytes, 0, BYTES_PER_DOUBLE);
            ObstacleMessage message = new ObstacleMessage();
            message.setAngle(ByteBuffer.wrap(angleBytes).getDouble());
            message.setObstacleDetected(buf[8] != 0);
            message.setObstacleAcknowledged(buf[9] != 0);
            return message;
        } catch (SocketException socketClosedError) {
            //ignore
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendAcknowledgedMessage() {
        sendMessageToObstacleDetection(ACKNOWLEDGED_INDEX);
    }

    public void sendDetectedMessage() {
        sendMessageToObstacleDetection(DETECTED_INDEX);
    }

    private void sendMessageToObstacleDetection(int detectedIndex) {
        try {
            DatagramSocket s = new DatagramSocket(6911, InetAddress.getByName(LOCAL_IP_ADDRESS));
            try {
                DatagramSocket socket = new DatagramSocket();
                InetAddress address = InetAddress.getByName(LOCAL_IP_ADDRESS);
                int port = 6911;
                byte[] byteBuffer = new byte[16];
                byteBuffer[detectedIndex] = 1;
                DatagramPacket packet = new DatagramPacket(byteBuffer, 16, address, port);
                socket.send(packet);
            } finally {
                s.close();
            }
        } catch (IOException ioexc) {
            ioexc.printStackTrace();
        }
    }

}
