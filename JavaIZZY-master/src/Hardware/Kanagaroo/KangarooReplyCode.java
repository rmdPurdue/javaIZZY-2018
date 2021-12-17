package Hardware.Kanagaroo;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Hardware.Kanagaroo
 * @date 12/12/2017
 */
public enum KangarooReplyCode {
    STATUS (0x43);

    private int status;

    KangarooReplyCode(int status) {
        this.status = status;
    }

    public int getReplyCode() {
        return this.status;
    }

    public static KangarooReplyCode fromInteger(int x) {
        switch(x) {
            case 0x43:
                return STATUS;
            default:
                return STATUS;
        }
    }
}
