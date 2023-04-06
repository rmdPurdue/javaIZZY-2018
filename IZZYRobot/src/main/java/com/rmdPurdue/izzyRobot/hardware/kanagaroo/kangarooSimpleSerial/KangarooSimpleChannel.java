package com.rmdPurdue.izzyRobot.hardware.kanagaroo.kangarooSimpleSerial;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Rich Dionne
 * @project JavaIZZY
 * @package com.rmdPurdue.Hardware.Kanagaroo.KangarooSimpleSerial
 * @date 12/18/2017
 */
public class KangarooSimpleChannel {
    private static final Logger log = LogManager.getLogger(KangarooSimpleChannel.class);

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
        String command = ("p" + units + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void P(int units, int speed) {
        String command = ("p" + units + " s" + speed + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void S(int units) {
        String command = ("s" + units + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void PI(int units) {
        String command = ("pi" + units + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void PI(int units, int speed) {
        String command = ("pi" + units + " s" + speed + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void SI(int units) {
        String command = ("si" + units + '\r' + '\n');
        kangaroo.write(this, command);
    }
    public void units(String unit){
        String command = (" units " + unit + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void getS() {
        String command = ("gets" + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void getP() {
        String command = ("getp" + '\r' + '\n');
        kangaroo.write(this, command);
    }

    public void start(){
        String command =("start" + '\r' + '\n');
        kangaroo.write(this, command);
        log.debug("System has started\n");
    }

    public void powerDown() {
        String command = ("powerdown" + '\r' + '\n');
        kangaroo.write(this, command);
    }
}
