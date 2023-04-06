package com.rmdPurdue.izzyRobot.motherCommunication.heartbeat;

public interface HeartbeatResponseListener {
    void onRemoteDeviceResponseReceived(MotherStatus motherStatus);
    void onMotherDeviceTimeout();
}
