package Kanagaroo;

import Kanagaroo.Enumerations.KangarooError;
import Kanagaroo.Enumerations.KangarooGetType;
import Kanagaroo.Enumerations.KangarooStatusFlags;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/11/2017
 */
public class KangarooStatus {

    private static KangarooStatus invalidStatus = createFromError(KangarooError.INVALID_STATUS);
    private static KangarooStatus portNotOpen = createFromError(KangarooError.PORT_NOT_OPEN);
    private static KangarooStatus timedOut = createFromError(KangarooError.TIMED_OUT);
    private byte channel;
    private byte echoCode;
    private KangarooError error;
    private byte flags;
    private boolean isValid;
    private byte sequenceCode;
    private byte type;
    private int value;

    private KangarooStatus() {
        init();
    }

    public KangarooStatus(byte[] buffer, int offset, int count) {
        init();
        if(parse(buffer, offset, count)) { this.isValid = true; } else { init(); }

    }

    private void init() {
        init(false, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0);
    }

    private void init(boolean isValid, byte channel, byte flags, byte echoCode, byte sequenceCode, byte type, byte value) {
        this.isValid = isValid;
        this.channel = channel;
        this.flags = flags;
        this.echoCode = echoCode;
        this.sequenceCode = sequenceCode;
        this.type = type;
        this.value = value;
    }

    private boolean parse(byte[] buffer, int offset, int count) {
        KangarooReplyReader parser = new KangarooReplyReader(buffer, offset, count);
        if(!parser.canRead()) { this.channel = 0; return false; }
        else { this.channel = parser.read(); }

        if(!parser.canRead()) { this.flags = 0; return false; }
        else { this.flags = parser.read(); }

        if(0 != (this.flags & (byte)KangarooStatusFlags.ECHO_CODE.getValue())) {
            if(!parser.canRead()) { this.echoCode = 0; return false; }
            else { this.echoCode = parser.read(); }
        }

        if(0 != (this.flags & (byte)KangarooStatusFlags.SEQUENCE_CODE.getValue())) {
            if(!parser.canRead()) { this.sequenceCode = 0; return false; }
            else { this.sequenceCode = parser.read(); }
        }

        if(!parser.canRead()) { this.type = 0; return false; }
        else { this.type = parser.read(); }

        this.value = parser.readBitPackedNumber();
        if(0 != (this.flags & (byte)KangarooStatusFlags.ERROR.getValue())) {
            if(this.value <= 0) { return false; }
        }

        return true;
    }

    public static KangarooStatus createInvalidStatus() {
        return invalidStatus;
    }

    public static KangarooStatus createPortNotOpen() {
        return portNotOpen;
    }

    public static KangarooStatus createTimedOut() {
        return timedOut;
    }

    private static KangarooStatus createFromError(KangarooError error) {
        int value = error.getValue();
        byte flags = (byte) KangarooStatusFlags.ERROR.getValue();
        KangarooStatus status = new KangarooStatus();
        status.init(true, (byte)0, (byte)flags, (byte)0, (byte)0, (byte)0, (byte)value);
        return status;
    }

    public char getChannel() {
        return (char)this.channel;
    }

    public byte getEchoCode() {
        return this.echoCode;
    }

    public KangarooError getError() {
        return isError() ? KangarooError.fromInteger(this.value) : KangarooError.NONE;
    }

    public KangarooStatusFlags getFlags() {
        return KangarooStatusFlags.fromInteger(flags);
    }

    public boolean isBusy() {
        return 0!= (getFlags().getValue() & KangarooStatusFlags.BUSY.getValue());
    }

    public boolean isDone() {
        return !isBusy();
    }

    public boolean isError() {
        return 0 != (getFlags().getValue() & KangarooStatusFlags.ERROR.getValue());
    }

    public boolean isOK() {
        return !isError();
    }

    public boolean isValid() {
        return this.isValid;
    }

    public byte getSequenceCode() {
        return this.sequenceCode;
    }

    public boolean isTimedOut() {
        return error == KangarooError.TIMED_OUT;
    }

    public KangarooGetType getType() {
        return KangarooGetType.fromInteger(this.type);
    }

    public int getValue() {
        return this.value;
    }
}
