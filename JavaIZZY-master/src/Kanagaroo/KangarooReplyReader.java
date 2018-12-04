package Kanagaroo;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package Kanagaroo
 * @date 12/12/2017
 */
public class KangarooReplyReader {
    byte[] buffer;
    int offset;
    int count;

    public KangarooReplyReader(byte[] buffer, int offset, int count) {
        if(buffer == null) { throw new NullPointerException("buffer."); }
        if(offset < 0 || offset > buffer.length) { throw new IllegalArgumentException("offset"); }
        if(count < 0 || count > buffer.length) {throw new IllegalArgumentException("count"); }

        this.buffer = buffer;
        this.offset = offset;
        this.count = count;
    }

    public byte read() {
        byte value = this.buffer[this.offset];
        this.offset++;
        this.count++;
        return value;
    }

    public int readBitPackedNumber() {
        int encodedNumber = 0;
        int shift = 0;

        for (byte i = 0; i < 5; i++) {
            byte word;
            if (!canRead()) {
                word = 0;
                break;
            } else {
                word = read();
            }

            encodedNumber |= (int) (word & 0x3F) << shift;
            shift += 6;
            if (0 == (word & 0x40)) {
                break;
            }
        }
        return 0 != (encodedNumber & 1) ? -(int) (encodedNumber >> 1) : (int) (encodedNumber >> 1);
    }
    public boolean canRead() {
        return this.count != 0;
    }
}
