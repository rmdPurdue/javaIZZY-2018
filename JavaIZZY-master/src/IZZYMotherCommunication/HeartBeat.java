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

    private final MulticastSocket listenerSocket; // MulticastSocket to listen for IZZYMother HeartbeatSender
    private final DatagramSocket responseSocket; // DatagramSocket to respond to IZZYMother
    private final InetAddress multicastAddress; // Multicast IP address to join
    private final AtomicBoolean isRunning; // Is IZZY supposed to be running
    private final AtomicBoolean isHeartBeating; // Is IZZY hearing mother's heartbeat
    private final IZZY izzy; // attributes to identify IZZY

    public HeartBeat(final IZZY izzy, final AtomicBoolean isRunning, final AtomicBoolean isHeartBeating) throws IOException {
        this.listenerSocket = new MulticastSocket(UDP_RECEIVE_PORT.getValue());
        this.responseSocket = new DatagramSocket(UDP_SEND_PORT.getValue()); // the only purpose of specifying a port is so we can manually allow it through the firewall if needed
        this.multicastAddress = InetAddress.getByName("239.0.0.57");
        this.isRunning = isRunning;
        this.isHeartBeating = isHeartBeating;
        this.izzy = izzy;
    }

    public void stopBeating() {
        isHeartBeating.set(false);
        try {
            listenerSocket.leaveGroup(multicastAddress);
            listenerSocket.close();
            responseSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        try {
            listenerSocket.joinGroup(multicastAddress);
            listenerSocket.setSoTimeout(izzy.getHeartbeatInterval());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while(isRunning.get()) {
            System.out.print("*");
            listenForHeartbeat();
        }
        stopBeating();
    }

    private void listenForHeartbeat() {
        byte[] buf = new byte[256];

        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        try {
           listenerSocket.receive(receivedPacket);
        } catch (SocketTimeoutException timeout) {
            isHeartBeating.set(false);
            //TODO: Check how long it has been since heartbeat
            System.out.print("X");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        InetAddress packetAddress = receivedPacket.getAddress();

        try {
            HeartbeatMessage message = new HeartbeatMessage(Arrays.copyOfRange(receivedPacket.getData(), 0, receivedPacket.getLength()));
            if (getMessageType(message.getMessageType()) == HELLO) {
                izzy.getMother().setIpAddress(packetAddress); //TODO: Once set, needs to be permanent?
                izzy.getMother().setUUID(message.getSenderUUID()); //TODO: Once set, needs to be permanent?
                isHeartBeating.set(true);
                respond();
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Not a valid message.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respond() throws IOException {
        HeartbeatMessage message = new HeartbeatMessage(HERE.getValue());
        message.setSenderUUID(izzy.getUUID());
        message.setData(izzy.getName().getBytes()); //TODO: Izzy doesn't need to know her name. We can send errors here
        DatagramPacket responsePacket = new DatagramPacket(message.getMessage(), message.getMessage().length, izzy.getMother().getIpAddress(), UDP_SEND_PORT.getValue());
        responseSocket.send(responsePacket);
    }

}
