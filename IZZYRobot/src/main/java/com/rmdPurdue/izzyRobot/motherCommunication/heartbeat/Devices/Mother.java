package com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.Devices;

import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.MotherStatus;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package util
 * @date 12/8/2018
 */
public class Mother {

    private final UUID uuid;
    private InetAddress ipAddress;
    private MotherStatus status;
    private long lastContact;

    public Mother(UUID uuid, InetAddress ipAddress) {
        this.uuid = uuid;
        this.ipAddress = ipAddress;
    }

    public UUID getUUID() {
        return uuid;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public MotherStatus getStatus() {
        return status;
    }

    public void setStatus(MotherStatus status) {
        this.status = status;
    }

    public long getLastContact() {
        return lastContact;
    }

    public void setLastContact(long lastContact) {
        this.lastContact = lastContact;
    }
}
