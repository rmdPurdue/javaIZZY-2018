package Kanagaroo.Enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo.Enumerations
 * @date 12/11/2017
 */
public enum KangarooCommand {
    START (0x20),
    UNITS (0x21),
    HOME (0x22),
    STATUS (0x23),
    MOVE (0x24),
    SYSTEM (0x25);

    private int command;

    KangarooCommand(int command) {
        this.command = command;
    }

    private KangarooCommand fromInteger(int x) {
        switch(x) {
            case 0x20:
                return START;
            case 0x21:
                return UNITS;
            case 0x22:
                return HOME;
            case 0x23:
                return STATUS;
            case 0x24:
                return MOVE;
            case 0x25:
                return SYSTEM;
            default:
                return START;
        }
    }

    public int getValue() {
        return this.command;
    }

}
