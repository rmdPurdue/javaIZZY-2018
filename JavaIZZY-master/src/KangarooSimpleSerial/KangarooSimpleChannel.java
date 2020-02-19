package KangarooSimpleSerial;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package KangarooSimpleSerial
 * @date 12/18/2017
 */
public class KangarooSimpleChannel {
    byte[] data;
    char name;
    KangarooSerial kangaroo;

    public KangarooSimpleChannel(KangarooSerial serial, char name) {
        this.kangaroo = serial;
        this.name = name;
    }

    public char getName() {
        return name;
    }

    public void P(int units) {
        String command = ("p" + Integer.toString(units) + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void S(int units) {
        String command = ("s" + Integer.toString(units) + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void PI(int units) {
        String command = ("pi" + Integer.toString(units) + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void SI(int units) {
        String command = ("si" + Integer.toString(units) + '\r' + '\n');
        kangaroo.write(this, command);
    }
    public void units(String unit){
        String command = (unit);
        kangaroo.write(this, command);
    }

    public void getS() {
        String command = ("gets" + '\r');
        kangaroo.write(this, command);
    }

    public void getP() {
        String command = ("getp" + '\r');
        kangaroo.write(this, command);
    }

    public void start(){
        String command =("start" + '\r');
        kangaroo.write(this, command);
        System.out.println("System has started\n");
    }
}
