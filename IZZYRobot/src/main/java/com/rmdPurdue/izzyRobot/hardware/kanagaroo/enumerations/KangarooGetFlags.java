package com.rmdPurdue.izzyRobot.hardware.kanagaroo.enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package com.rmdPurdue.Hardware.Kanagaroo
 * @date 12/11/2017
 */
public enum KangarooGetFlags {
    DEFAULT (0),
    ECHO_CODE (16),
    RAW_UNITS (32),
    SEQUENCE_CODE(64);

    private int flag;

    KangarooGetFlags(int flag) {
        this.flag = flag;
    }

    public int getValue() {
        return flag;
    }

    public static KangarooGetFlags fromInteger(int x) {
        switch(x) {
            case 0:
                return DEFAULT;
            case 16:
                return ECHO_CODE;
            case 32:
                return RAW_UNITS;
            case 64:
                return SEQUENCE_CODE;
            default:
                return DEFAULT;
        }
    }

}
