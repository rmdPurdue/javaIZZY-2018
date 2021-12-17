package Hardware.Kanagaroo.Enumerations;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Hardware.Kanagaroo
 * @date 12/11/2017
 */
public enum KangarooGetType {
    P (1),
    PI (65),
    S (2),
    SI (66),
    MIN (8),
    MAX (9);

    private int type;

    KangarooGetType(int type) {
        this.type = type;
    }

    public static KangarooGetType fromInteger(int x) {
        switch(x) {
            case 1:
                return P;
            case 65:
                return PI;
            case 2:
                return S;
            case 66:
                return SI;
            case 8:
                return MIN;
            case 9:
                return MAX;
            default:
                return P;
        }
    }

    public int getValue() {
        return type;
    }

}
