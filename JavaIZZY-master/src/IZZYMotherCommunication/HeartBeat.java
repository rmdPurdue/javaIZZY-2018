package IZZYMotherCommunication;

import Devices.IZZY;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import static IZZYMotherCommunication.MessageType.*;
import static IZZYMotherCommunication.PortEnumerations.*;

/**
 * @author Rich Dionne
 * @project JavaMother
 * @package comms
 * @date 12/6/2018
 */
public class HeartBeat implements Runnable {

    private HeartBeatListener listener;
    private final MulticastSocket listenerSocket;
    private final DatagramSocket responseSocket;
    private final AtomicBoolean running;
    private final AtomicBoolean heartBeating;
    private final IZZY izzy;
    private long lastHeartBeatTime;

    public HeartBeat(final IZZY izzy, final AtomicBoolean running, final AtomicBoolean heartBeating) throws IOException {
        this.listener = null;
        this.listenerSocket = new MulticastSocket(UDP_RECEIVE_PORT.getValue());
        this.responseSocket = new DatagramSocket();
        this.running = running;
        this.heartBeating = heartBeating;
        this.izzy = izzy;
        this.lastHeartBeatTime = 0;
    }

    public void setListener(HeartBeatListener listener) {
        this.listener = listener;
    }

    public void stopBeating() {
        running.set(false);
        listenerSocket.close();
        responseSocket.close();
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        InetAddress group;

        try {
            group = InetAddress.getByName("239.0.0.57");
            listenerSocket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
            heartBeating.set(false);
            return;
        }

        lastHeartBeatTime = System.currentTimeMillis();

        while(running.get()) {
            System.out.println("Listening");
            listenForHeartbeat();
        }
        heartBeating.set(false);
        System.out.println("WE FINISHED");
    }

    private void listenForHeartbeat() {
        byte[] buf = new byte[256];

        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        try {
           listenerSocket.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress packetAddress = receivedPacket.getAddress();

        HeartbeatMessage message;

        try {
            message = new HeartbeatMessage(Arrays.copyOfRange(receivedPacket.getData(), 0, receivedPacket.getLength()));
            if(System.currentTimeMillis() - lastHeartBeatTime <= izzy.getHeartbeatInterval()) {
                switch (getMessageType(message.getMessageType())) {
                    case HELLO:
                        izzy.getMother().setIpAddress(packetAddress);
                        izzy.getMother().setUUID(message.getSenderUUID());
                        respond();
                        listener.onHeartBeatReceived();
                        lastHeartBeatTime = System.currentTimeMillis();
                        break;
                    case NOT_VALID:
                        break;
                }
            } else {
                heartBeating.set(false);
                listener.onIntervalTimeOut();
            }
            lastHeartBeatTime = System.currentTimeMillis();
        } catch (IllegalArgumentException e) {
            System.out.println("Not a valid message.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respond() throws IOException {
        //System.out.println("Sent response.");
        HeartbeatMessage message = new HeartbeatMessage();
        message.setSenderUUID(izzy.getUUID());
        message.setMessageType(HERE.getValue());
        message.setData(izzy.getName().getBytes());
        DatagramPacket responsePacket = new DatagramPacket(message.getMessage(), message.getMessage().length, izzy.getMother().getIpAddress(), UDP_SEND_PORT.getValue());
        responseSocket.send(responsePacket);
    }

}
