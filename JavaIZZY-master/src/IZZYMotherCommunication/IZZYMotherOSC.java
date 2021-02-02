package IZZYMotherCommunication;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

import java.net.SocketException;

public abstract class IZZYMotherOSC {

    private final OSCPortIn receiver;

    public IZZYMotherOSC() throws SocketException {
        this.receiver = new OSCPortIn(9000);
    }

    public void addListener(final String address, final OSCListener listener) {
        receiver.addListener(address, listener);
    }

    public void startListening() {
        if(!receiver.isListening()) {
            receiver.startListening();
        }
    }

    public void close() {
        if(receiver.isListening()) {
            receiver.close();
        }
    }

    public abstract void eStopOSC(final OSCMessage msg);


}
