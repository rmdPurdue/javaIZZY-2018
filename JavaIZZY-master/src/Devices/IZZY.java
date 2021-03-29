package Devices;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package util
 * @date 12/6/2018
 */
public class IZZY {
    private final UUID uuid;
    private String name;
    private InetAddress ipAddress;
    private int heartbeatInterval;
    private long lastHeartbeatTime;
    private Mother mother;

    public IZZY(final String name, final int heartbeatInterval) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.ipAddress = null;
        this.heartbeatInterval = heartbeatInterval;
        this.lastHeartbeatTime = 0;
        this.mother = new Mother();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public Mother getMother() {
        return mother;
    }

    public void setMother(Mother mother) {
        this.mother = mother;
    }
}
