package IZZYMotherCommunication;

/**
 * @author Rich Dionne
 * @project JavaMother
 * @package comms
 * @date 12/6/2018
 */
public enum PortEnumerations {
    UDP_SEND_PORT(9000),
    UDP_RECEIVE_PORT(9001),
    OSC_SEND_PORT(8001),
    OSC_RECEIVE_PORT(8000);

    private int port;

    PortEnumerations(int port) {
        this.port = port;
    }

    public int getValue() {
        return port;
    }
}
