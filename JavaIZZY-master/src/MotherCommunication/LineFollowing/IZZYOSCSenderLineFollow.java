package MotherCommunication.LineFollowing;

import MotherCommunication.Heartbeat.HeartbeatResponder;
import MotherCommunication.PortEnumerations;
import Movement.LineFollowing.IZZYMoveLineFollow;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.SocketException;

public class IZZYOSCSenderLineFollow {

    private final OSCPortOut sender;
    private final IZZYMoveLineFollow izzyMove;

    public IZZYOSCSenderLineFollow(IZZYMoveLineFollow izzyMove, HeartbeatResponder heartBeat) throws SocketException {
        this.izzyMove = izzyMove;
        this.sender = new OSCPortOut(heartBeat.getMotherIpAddress(), PortEnumerations.OSC_SEND_PORT.getValue());
    }

    public void sendData() throws IOException {
        OSCMessage outgoingMessage = new OSCMessage();
        outgoingMessage.setAddress("/IZZYMother/Status");
        outgoingMessage.addArgument(izzyMove.getSpeedValue());
        outgoingMessage.addArgument(izzyMove.getPidValue());
        outgoingMessage.addArgument(izzyMove.getErrorAngle());
        outgoingMessage.addArgument(izzyMove.getKp());
        outgoingMessage.addArgument(izzyMove.getKi());
        outgoingMessage.addArgument(izzyMove.getKd());
        outgoingMessage.addArgument(izzyMove.isMoving());
        outgoingMessage.addArgument(izzyMove.isSensor0());
        outgoingMessage.addArgument(izzyMove.isSensor1());
        outgoingMessage.addArgument(izzyMove.isSensor2());
        outgoingMessage.addArgument(izzyMove.getSensorsAnalog()[0]);
        outgoingMessage.addArgument(izzyMove.getSensorsAnalog()[1]);
        outgoingMessage.addArgument(izzyMove.getSensorsAnalog()[2]);
        outgoingMessage.addArgument(izzyMove.getSensorThresholds()[0]);
        outgoingMessage.addArgument(izzyMove.getSensorThresholds()[1]);
        outgoingMessage.addArgument(izzyMove.getSensorThresholds()[2]);
        sender.send(outgoingMessage);
    }

    public void close() {
        sender.close();
    }
}
