package com.rmdPurdue.izzyRobot.motherCommunication.heartbeat;

import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.Devices.IZZY;
import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.Devices.Mother;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rmdPurdue.izzyRobot.motherCommunication.PortEnumerations.*;
import static com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.MessageType.*;

/**
 * @author Rich Dionne
 * @project JavaMother
 * @package comms
 * @date 12/6/2018
 */
public class HeartbeatResponder implements Runnable {
    private static final Logger log = LogManager.getLogger(HeartbeatResponder.class);

    private static HeartbeatResponder currentInstance;
    private final DatagramSocket listenerSocket; // DatagramSocket to listen for IZZYMother HeartbeatSender
    private final DatagramSocket responseSocket; // DatagramSocket to respond to IZZYMother
    private final AtomicBoolean isRunning; // Is IZZY supposed to be running
    private IZZY izzy; // attributes to identify IZZY
    private HeartbeatResponseListener listener; // listener to take action
    private String errorMessage; // A short fault message if applicable
    private MessageType messageType; // The message type which will dictate how the message is processed by mother
    private boolean isHeartbeating; // Is IZZY hearing mother's heartbeat

    public HeartbeatResponder(final AtomicBoolean isRunning) throws IOException {
        currentInstance = this;
        this.listenerSocket = new DatagramSocket(UDP_RECEIVE_PORT.getValue());
        this.responseSocket = new DatagramSocket();
        this.isRunning = isRunning;
        this.izzy = null;
        this.listener = null;
        this.errorMessage = "";
        this.messageType = HERE;
        isHeartbeating = false;
    }

    @Override
    public void run() {
        try {
            listenerSocket.setSoTimeout(1000); // 1 seconds, no comm from Mother
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while(isRunning.get()) {
            listenForHeartbeat();
        }
        stopBeating();
    }

    public void stopBeating() {
        isHeartbeating = false;
        listenerSocket.close();
        responseSocket.close();
        Thread.currentThread().interrupt();
    }

    public void setListener(HeartbeatResponseListener listener) {
        this.listener = listener;
    }

    public static HeartbeatResponder getCurrentInstance() {
        return currentInstance;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public InetAddress getMotherIpAddress() {
        return izzy.getMother().getIpAddress();
    }

    public boolean isHeartbeating() {
        return isHeartbeating;
    }

    private void listenForHeartbeat() {
        byte[] buf = new byte[256];

        DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
        try {
           listenerSocket.receive(receivedPacket);
           processPacket(receivedPacket);
        } catch (SocketTimeoutException timeout) {
            isHeartbeating = false;
            if (listener != null) {
                listener.onMotherDeviceTimeout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processPacket(DatagramPacket receivedPacket) {
        HeartbeatMessage message;
        try {
            message = new HeartbeatMessage(Arrays.copyOfRange(receivedPacket.getData(), 0, receivedPacket.getLength()));
        } catch (IllegalArgumentException e) {
            log.error("Not a valid message");
            return;
        }

        if (izzy == null) {
            izzy = new IZZY(message.getReceiverUUID());
            izzy.setMother(new Mother(message.getSenderUUID(), receivedPacket.getAddress()));
        }

        if (!verifyMessage(message, receivedPacket.getAddress())) {
            return;
        }

        Mother mother = izzy.getMother();
        mother.setLastContact(System.currentTimeMillis());

        if (getMessageType(message.getMessageType()) == HELLO) {
            isHeartbeating = true;
            mother.setStatus(MotherStatus.CONNECTED);
            if (listener != null) {
                listener.onRemoteDeviceResponseReceived(MotherStatus.CONNECTED);
            }
            respond();
        } else {
            throw new IllegalArgumentException();
        }

    }

    private boolean verifyMessage(HeartbeatMessage message, InetAddress packetIp) {
        Mother mother = izzy.getMother();
        return (mother.getIpAddress().equals(packetIp)
                && message.getSenderUUID().equals(mother.getUUID())
                && message.getReceiverUUID().equals(izzy.getUUID()));
    }

    public void respond() {
        HeartbeatMessage message = new HeartbeatMessage(messageType.getValue());
        message.setSenderUUID(izzy.getUUID());
        message.setReceiverUUID(izzy.getMother().getUUID());
        if (errorMessage != null) {
            message.setData(errorMessage.getBytes());
        }
        try {
            DatagramPacket responsePacket = new DatagramPacket(message.getMessage(), message.getMessage().length,
                    izzy.getMother().getIpAddress(), UDP_SEND_PORT.getValue());
            responseSocket.send(responsePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
