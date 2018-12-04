package Kanagaroo.Enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/11/2017
 */
public enum KangarooMoveFlags {
    DEFAULT (0),
    NO_DEFAULT_LIMITS (8),
    RAW_UNITS (32),
    SEQUENCE_CODE (64);

    private int moveFlag;

    KangarooMoveFlags(int moveFlag) {
        this.moveFlag = moveFlag;
    }

    public static KangarooMoveFlags fromInteger(int x) {
        switch(x) {
            case 0:
                return DEFAULT;
            case 8:
                return NO_DEFAULT_LIMITS;
            case 32:
                return RAW_UNITS;
            case 64:
                return SEQUENCE_CODE;
            default:
                return DEFAULT;
        }
    }

    public int getValue() {
        return this.moveFlag;
    }

}
