package Hardware.Kanagaroo;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Hardware.Kanagaroo
 * @date 12/11/2017
 */
public class KangarooCRC {

    private int crc;

    public void begin() {
        this.crc = 0x3FFF;
    }

    public void write(byte data) {
        this.crc ^= (byte) (data & 0x7F);

        for(int bit = 0; bit < 7; bit++) {
            if(0 != (this.crc & 1)) {
                this.crc >>= 1;
                this.crc ^= 0x22F0;
            } else {
                this.crc >>= 1;
            }
        }
    }

    public void write(byte[] data, int offset, int length) {
        for(int i = 0; i < length; i++) {
            write(data[offset + 1]);
        }
    }

    public void end() {
        this.crc ^= 0x3FFF;
    }

    public int value() {
        return this.crc;
    }

    public void value(int crc) {
        this.crc = crc;
    }

    public static int value(byte[] data, int offset, int length) {
        KangarooCRC crc = new KangarooCRC();
        crc.begin();
        crc.write(data, offset, length);
        crc.end();
        return crc.value();
    }
}
