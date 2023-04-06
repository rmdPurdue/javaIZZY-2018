package com.rmdPurdue.izzyRobot.hardware.kanagaroo.enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package com.rmdPurdue.Hardware.Kanagaroo
 * @date 12/11/2017
 */
public enum KangarooStatusFlags {
    NONE (0),
    ERROR (1),
    BUSY (2),
    ECHO_CODE (16),
    RAW_UNITS (32),
    SEQUENCE_CODE (64);

    private int statusFlag;

    KangarooStatusFlags(int statusFlag) {
        this.statusFlag = statusFlag;
    }

    public static KangarooStatusFlags fromInteger(int x) {
        switch(x) {
            case 0:
                return NONE;
            case 1:
                return ERROR;
            case 2:
                return BUSY;
            case 16:
                return ECHO_CODE;
            case 32:
                return RAW_UNITS;
            case 64:
                return SEQUENCE_CODE;
            default:
                return NONE;
        }
    }

    public int getValue() {
        return statusFlag;
    }
}
