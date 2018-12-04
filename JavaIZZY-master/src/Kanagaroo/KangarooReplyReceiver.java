package Kanagaroo;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/12/2017
 */
public class KangarooReplyReceiver {
    private int length;
    private boolean ready;
    private byte[] data = new byte[KangarooConstants.COMMAND_MAX_BUFFER_LENGTH];

    public KangarooReplyReceiver() {
        reset();
    }

    public void read(byte word) {
        if(word >= 128 || this.ready) { reset(); }
        if(this.length < KangarooConstants.COMMAND_MAX_BUFFER_LENGTH) { this.data[this.length++] = word; }
        if(this.length >= 5 && this.length -5 == this.data[2]) {
            if(KangarooCRC.value(this.data, 0, this.length) == KangarooConstants.CRC_GOOD_VALUE) {
                this.ready = true;
            }
        }
    }

    private void reset() {
        this.length = 0;
        this.ready = false;
    }

    public int getAddress() {
        return this.data[0];
    }

    public byte[] getBuffer() {
        return this.data;
    }

    public KangarooReplyCode getCommand() {
        return KangarooReplyCode.fromInteger(this.data[1]);
    }

    public boolean isReady() {
        return this.ready;
    }

    public int getOffset() {
        return 3;
    }

    public int getLength() {
        return length - 5;
    }
}
