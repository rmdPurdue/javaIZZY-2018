package Kanagaroo.Enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/11/2017
 */
public enum KangarooError {
    NONE (0x00),
    NOT_STARTED (0x01),
    NOT_HOMED (0x02),
    CONTROL_ERROR (0x03),
    WRONG_MODE (0x04),
    UNRECOGNIZED_CODE (0x05),
    SERIAL_TIMEOUT (0x06),
    INVALID_STATUS (-0x01),
    TIMED_OUT (-0x02),
    PORT_NOT_OPEN (-0x03);

    private int error;

    KangarooError(int error) {
        this.error = error;
    }

    public static KangarooError fromInteger(int x) {
        switch(x) {
            case 0x00:
                return NONE;
            case 0x01:
                return NOT_STARTED;
            case 0x02:
                return NOT_HOMED;
            case 0x03:
                return CONTROL_ERROR;
            case 0x04:
                return WRONG_MODE;
            case 0x05:
                return UNRECOGNIZED_CODE;
            case 0x06:
                return SERIAL_TIMEOUT;
            case -0x01:
                return INVALID_STATUS;
            case -0x02:
                return TIMED_OUT;
            case -0x03:
                return PORT_NOT_OPEN;
            default:
                return NONE;
        }
    }

    public int getValue() {
        return this.error;
    }

}
