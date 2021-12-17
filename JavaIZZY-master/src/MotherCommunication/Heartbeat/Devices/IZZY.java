package MotherCommunication.Heartbeat.Devices;

import MotherCommunication.Heartbeat.IZZYStatus;

import java.net.InetAddress;
import java.util.UUID;

import static MotherCommunication.Heartbeat.IZZYStatus.*;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package util
 * @date 12/6/2018
 */
public class IZZY {
    private final UUID uuid;
    private InetAddress ipAddress;
    private IZZYStatus status;
    private Mother mother;

    public IZZY(UUID uuid) {
        this.uuid = uuid;
        this.ipAddress = null;
        this.status = MISSING;
        this.mother = null;
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

    public IZZYStatus getStatus() {
        return status;
    }

    public void setStatus(IZZYStatus status) {
        this.status = status;
    }

    public Mother getMother() {
        return mother;
    }

    public void setMother(Mother mother) {
        this.mother = mother;
    }

    @Override
    public boolean equals(Object izzy) {
        if (izzy instanceof IZZY) {
            return ((IZZY) izzy).getUUID().equals(this.getUUID());
        }
        return false;
    }
}
