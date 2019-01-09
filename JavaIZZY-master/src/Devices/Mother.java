package Devices;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package util
 * @date 12/8/2018
 */
public class Mother {

    private UUID uuid = null;
    private InetAddress ipAddress = null;

    public Mother() {
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }
}
