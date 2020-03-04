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

    public KangarooSimpleChannel D;
    public KangarooSimpleChannel T;

    IZZYPosition(KangarooSimpleChannel drive, KangarooSimpleChannel turn, double wheelRad, double systemRad, int encoderResolution, int motorRatio) {
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

        double readableDrive = (Math.PI * (wheelRad * 2)) * 10;
        double lineDrive = (encoderResolution * motorRatio) * 10;
        double lineAngle = Math.PI * (systemRad * 2) / readableDrive * lineDrive;

        this.D.units( (int)(readableDrive + 0.5) + " mm = " + (int)(lineDrive + 0.5) + " lines");
        this.T.units("360 degrees = " + (int)(lineAngle + 0.5) + " lines");

        this.D.P(0);
        this.T.P(0);

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
        System.out.println("turning to " + angleIn);
    }

    public void izzyMove(int distance) {
        this.D.P(distance);
        System.out.println("moving to " + distance);
    }

    public int izzySimpleMove(int x, int y, int z, int clockwise) {
        //TODO: Implement movement based on PIDValue (use different drive mode?). Kangaroo documentation helpful!

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