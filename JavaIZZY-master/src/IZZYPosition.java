import KangarooSimpleSerial.KangarooSimpleChannel;

import java.util.ArrayList;
public class IZZYPosition {

    private int homex = 0;
    private int homey = 0;
    private int homez = 0;

    private int positionx = 0;
    private int positiony = 0;
    private int positionz = 0;
    private int angle = 0;
    private int wheelrad = 0;
    private int systemrad = 0;
    private int linesPerRotation = 0;

    public KangarooSimpleChannel D;
    public KangarooSimpleChannel T;

    IZZYPosition(KangarooSimpleChannel drive, KangarooSimpleChannel turn, int wheelrad, int systemrad, int linesPerRotation) {
        this.homex = 0;
        this.homey = 0;
        this.homez = 0;
        this.positionx = 0;
        this.positiony = 0;
        this.positionz = 0;
        this.angle = 0;
        this.D = drive;
        this.T = turn;
        this.D.start();
        this.T.start();
        D.units("1995 mm = 19200 lines");
        T.units("360 degrees = 4148 lines");
        this.D.P(0);
        this.T.P(0);
        //D.units("1 rotation = " + linesPerRotation + " lines"); // TODO: Calibrate for small IZZY
                                                                // (using Kangaroo Documentation and encoder resolution)
        this.wheelrad = wheelrad;
        this.systemrad = systemrad;
        this.linesPerRotation = linesPerRotation;
    }

    public ArrayList<Object> getPosition() {
        ArrayList tmp = new ArrayList<Object>();
        tmp.add(this.positionx);
        tmp.add(this.positiony);
        tmp.add(this.positionz);
        return tmp;
    }

    public int getAngle() {
        return this.angle;
    }

    public void updatePosition(int x, int y, int z) {
        this.positionx = x;
        this.positiony = y;
        this.positionz = z;
    }

    public void updateAngle(int angleIn) {
        this.angle = angleIn;
    }

    public void izzyTurn(int angleIn) {
        this.T.P(angleIn);
        System.out.println("turning now to " + angleIn);
        int lineAngle = this.systemrad*360*this.linesPerRotation;
        if(angleIn == 0){
            lineAngle = 0;
        }
        else {
            lineAngle = lineAngle / (angleIn * this.wheelrad);
        }

    }

    public void izzyMove(int distance) {
        this.D.P(distance);
    }

    public int izzySimpleMove(int x, int y, int z, int clockwise) {
        double tanAngle;
        double CCWAngle;
        double CWAngle;
        double distance;
        if(this.positionx == x && this.positiony != y){
            CWAngle = 90;
            CCWAngle = 270;
        }
        else if(this.positiony == y){
            CWAngle = 0;
            CCWAngle = 0;
        }else {
            tanAngle = Math.atan((float)(this.positiony - y) / (float)(this.positionx - x)) * 360.0 / (2 * 3.14);
            System.out.println("tanAngle: " + tanAngle);
            if (x > this.positionx) {
                if (y > this.positiony) {
                    CCWAngle = tanAngle;
                    CWAngle = 360 - tanAngle;
                } else { //y < this.positiony
                    CCWAngle = 360 - tanAngle;
                    CWAngle = tanAngle;
                }
            } else { // x < this.positionx
                if (y > this.positiony) {
                    CCWAngle = 180 - tanAngle;
                    CWAngle = 180 + tanAngle;
                } else { //y < this.positiony
                    CCWAngle = 180 + tanAngle;
                    CWAngle = 180 - tanAngle;
                }
            }
        }

        if (clockwise == 1) {
            this.izzyTurn((int)-CWAngle);
        } else if (clockwise == -1) {
            this.izzyTurn((int)CCWAngle);
        } else { //shortest turn
            if (Math.abs(this.angle - CCWAngle) > Math.abs(this.angle - CWAngle)) {
                this.izzyTurn((int)-CWAngle);
            } else {
                this.izzyTurn((int)CCWAngle);
            }

        }
        System.out.println("moving now");
        distance = Math.sqrt(Math.pow(this.positionx - x,2) + Math.pow(this.positiony - y,2));
        this.izzyMove((int)distance);
        this.updatePosition(x,y,z);
        return 0;
    }
}