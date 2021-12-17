package MotherCommunication;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import java.net.SocketException;

import static MotherCommunication.PortEnumerations.OSC_RECEIVE_PORT;

public abstract class IZZYOSCReceiver {

    private final OSCPortIn receiver;

    public IZZYOSCReceiver() throws SocketException {
        this.receiver = new OSCPortIn(OSC_RECEIVE_PORT.getValue());
    }

    public void addListener(final String address, final OSCListener listener) {
        receiver.addListener(address, listener);
    }

    public void startListening() {
        if(!receiver.isListening()) {
            receiver.startListening();
        }
    }

    public void stopListening() {
        if(receiver.isListening()) {
            receiver.stopListening();
        }
    }

    public void close() {
        if(receiver.isListening()) {
            receiver.close();
        }
    }

    public abstract void eStopOSC(final OSCMessage msg);

}
