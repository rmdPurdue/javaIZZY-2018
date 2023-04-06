package com.rmdPurdue.izzyRobot.motherCommunication.lineFollowing;

import com.rmdPurdue.izzyRobot.exceptions.EStopException;
import com.rmdPurdue.izzyRobot.hardware.lineFollowing.DriveReadings;
import com.rmdPurdue.izzyRobot.illposed.osc.OSCMessage;
import com.rmdPurdue.izzyRobot.illposed.osc.OSCPortOut;
import com.rmdPurdue.izzyRobot.motherCommunication.PortEnumerations;
import com.rmdPurdue.izzyRobot.motherCommunication.heartbeat.HeartbeatResponder;
import com.rmdPurdue.izzyRobot.movement.lineFollowing.IZZYMoveLineFollow;

import java.io.IOException;
import java.net.SocketException;

public class IZZYOSCSenderLineFollow {

    private final OSCPortOut sender;
    private final IZZYMoveLineFollow izzyMove;
    private final DriveReadings driveReadings;

    public IZZYOSCSenderLineFollow(IZZYMoveLineFollow izzyMove, DriveReadings driveReadings,
                                   HeartbeatResponder heartBeat) throws SocketException {
        this.izzyMove = izzyMove;
        this.driveReadings = driveReadings;
        this.sender = new OSCPortOut(heartBeat.getMotherIpAddress(), PortEnumerations.OSC_SEND_PORT.getValue());
    }

    public void startDriveReadings() {
        driveReadings.startWheelReadingLoop();
    }

    public void sendData() throws IOException, EStopException {
        OSCMessage outgoingMessage = new OSCMessage();
        outgoingMessage.setAddress("/IZZYMother/Status");
        outgoingMessage.addArgument(izzyMove.getSpeedValue());
        outgoingMessage.addArgument(izzyMove.getPidValue());
        outgoingMessage.addArgument(izzyMove.getErrorAngle());
        outgoingMessage.addArgument(izzyMove.getKp());
        outgoingMessage.addArgument(izzyMove.getKi());
        outgoingMessage.addArgument(izzyMove.getKd());
        outgoingMessage.addArgument(izzyMove.isMoving());
        int[] sensorAnalog = izzyMove.getSensorsAnalog(); //potential I/O (if not moving)
        outgoingMessage.addArgument(sensorAnalog[0]);
        outgoingMessage.addArgument(0);
        outgoingMessage.addArgument(sensorAnalog[1]);
        int[] sensorThresholds = izzyMove.getSensorThresholds();
        outgoingMessage.addArgument(sensorThresholds[0]);
        outgoingMessage.addArgument(0);
        outgoingMessage.addArgument(sensorThresholds[1]);
        outgoingMessage.addArgument(driveReadings.getDriveP());
        outgoingMessage.addArgument(driveReadings.getDriveS());
        outgoingMessage.addArgument(driveReadings.getTurnP());
        outgoingMessage.addArgument(driveReadings.getTurnS());
        sender.send(outgoingMessage);
    }

    public void close() {
        sender.close();
    }
}
