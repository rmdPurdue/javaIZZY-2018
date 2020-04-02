package LineFollowing;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.Iterator;

public class SensorArray
{
    private int previousError;
    private int errorSum;
    private int error;
    private int kp;
    private int ki;
    private int kd;
    private double yDistance;
    private int pidValue;
    private boolean[] signalArray;
    private boolean estop;
    private boolean stop;

    ArrayList<LineSensor> sensorList = new ArrayList<>();

    public SensorArray(int kp, int ki, int kd, double yDistance) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.yDistance = yDistance;
        this.errorSum = 0;
        this.error = 0;
        this.signalArray = new boolean[3]; //3 sensor array (need to adjust readSensors() if adding more sensors)
        this.estop = false;
        this.stop = false;
    }

    public void addSensor(LineSensor sensor){
        sensorList.add(sensor);
    }

    public int getKp() {
        return this.kp;
    }

    public void setKp(int kp) {
        this.kp = kp;
    }

    public int getKi() {
        return this.ki;
    }

    public void setKi(int ki) {
        this.ki = ki;
    }

    public int getKd() {
        return this.kd;
    }

    public void setKd(int kd) {
        this.kd = kd;
    }

    public double getYDistance() {
        return this.yDistance;
    }

    public void setYDistance(double yDistance) {
        this.yDistance = yDistance;
    }

    public double getErrorAngle() {
        if (estop) {
            return 999;
        } else if (stop) {
            return 360;
        } else {
            return -(Math.atan(getPidValue() / yDistance));
        }
    }

    public void readSensors() {
        //TODO: Set error values to the distance of the wire from center (in milimeters?)
        if (!estop) {
            for (int i = 0; i < sensorList.size(); i++) {
                //System.out.println(sensorList.get(i).getAnalogInput().getName() + " = " + sensorList.get(i).getSensorReading());
                signalArray[i] = sensorList.get(i).getSensorState(); //checks if wire is under sensor
            }
            if (signalArray[0] && signalArray[1] && signalArray[2]) {           //  1   1   1
                stop = true;
            } else if (!signalArray[0] && !signalArray[1] && !signalArray[2]) {   //  -   -   -
                estop = true;
            } else if (signalArray[0] && !signalArray[1] && !signalArray[2]) {    //  1   -   -
                error = -2;
            } else if (signalArray[0] && signalArray[1] && !signalArray[2]) {     //  1   1   -
                error = -1;
            } else if (!signalArray[0] && signalArray[1] && !signalArray[2]) {    //  -   1   -
                error = 0;
            } else if (!signalArray[0] && signalArray[1] && signalArray[2]) {     //  -   1   1
                error = 1;
            } else if (!signalArray[0] && !signalArray[1] && signalArray[2]) {    //  -   -   1
                error = 2;
            } else {
                estop = true;
            }
        }
    }

    public void calculatePID() {
        int proportional = getKp() * error;
        int integral = getKi() * errorSum;
        int derivative = getKd() * previousError;
        pidValue = proportional + integral + derivative;
        if (pidValue > 180) {
            pidValue = 180;
        }
        errorSum += error;
        previousError = error;
    }

    public double getPidValue() {
        return pidValue;
    }
}
