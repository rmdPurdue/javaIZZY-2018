package Hardware.Kanagaroo.Enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Hardware.Kanagaroo
 * @date 12/11/2017
 */
public enum KangarooSystemCommand {
    POWER_DOWN (0),
    POWER_DOWN_ALL (1),
    TUNE_ENTER_MODE (3),
    TUNE_GO (4),
    TUNE_ABORT (5),
    TUNE_CONTROL_OPEN_LOOP (6),
    TUNE_SET_DISABLED_CHANNELS (8),
    SET_BAUD_RATE (32),
    SET_SERIAL_TIMEOUT (33);

    private int command;

    KangarooSystemCommand(int command) {
        this.command = command;
    }

    public static KangarooSystemCommand fromInteger(int x) {
        switch(x) {
            case 0:
                return POWER_DOWN;
            case 1:
                return POWER_DOWN_ALL;
            case 3:
                return TUNE_ENTER_MODE;
            case 4:
                return TUNE_GO;
            case 5:
                return TUNE_ABORT;
            case 6:
                return TUNE_CONTROL_OPEN_LOOP;
            case 8:
                return TUNE_SET_DISABLED_CHANNELS;
            case 32:
                return SET_BAUD_RATE;
            case 64:
                return SET_SERIAL_TIMEOUT;
            default:
                return POWER_DOWN;
        }
    }

    public int getCommand() {
        return command;
    }
}
