package MotherCommunication.Heartbeat;

public interface HeartbeatResponseListener {
    void onRemoteDeviceResponseReceived(MotherStatus motherStatus);
    void onMotherDeviceTimeout();
}
