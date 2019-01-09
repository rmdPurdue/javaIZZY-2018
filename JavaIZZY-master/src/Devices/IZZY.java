package Devices;

import KangarooSimpleSerial.KangarooSerial;
import KangarooSimpleSerial.KangarooSimpleChannel;
import util.IZZYStatus;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package util
 * @date 12/6/2018
 */
public class IZZY {

    private String name = null;
    private InetAddress ipAddress = null;
    private UUID myUUID = UUID.fromString("b7a5fbe1-6c7e-4dbe-ad13-42f0b2c60d29");
    private KangarooSerial kangarooSerial = null;
    private IZZYStatus status = null;
    private int heartbeatInterval = 250;
    private long lastHeartbeatTime;
    private Mother mother = null;

    public IZZY() {
        kangarooSerial = new KangarooSerial();
        status = IZZYStatus.AVAILABLE;
        mother = new Mother();
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

    public UUID getUUID() {
        return myUUID;
    }

    public KangarooSerial getKangarooSerial() {
        return kangarooSerial;
    }

    public void setStatus(IZZYStatus status) {
        this.status = status;
    }

    public IZZYStatus getStatus() {
        return status;
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

    public void setMother(Mother mother) {
        this.mother = mother;
    }

    public Mother getMother() {
        return mother;
    }
}
