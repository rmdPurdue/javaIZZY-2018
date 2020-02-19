package LineFollowing;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.Iterator;

public class SensorArray
{
    private double errorSum;
    private double error;
    private double kp;
    private double ki;
    private double kd;
    private double yDistance;
    private double pidValue;
    private double setpoint;
    private boolean rotating;

    ArrayList<LineSensor> sensorList = new ArrayList<>();

    public SensorArray(){
        errorSum = 0;
        error = 0;
        kp = 5;
        ki = 0.3;
        kd = 1;
        yDistance = 19.5;
    }

    public void addSensor(LineSensor sensor){
        sensorList.add(sensor);
    }

//    public LineSensor getSensor(int index){
//        return sensorList.get(index);
//    }

    public double getKp() {
        return this.kp;
    }

    public void setKp(float kp) {
        this.kp = kp;
    }

    public double getKi() {
        return this.ki;
    }

    public void setKi(float ki) {
        this.ki = ki;
    }

    public double getKd() {
        return this.kd;
    }

    public void setKd(float kd) {
        this.kd = kd;
    }

    public double getYDistance() {
        return this.yDistance;
    }

    public void setYDistance(double yDistance) {
        this.yDistance = yDistance;
    }

    public double getSetpoint() {
        return this.setpoint;
    }

    public void setSetpoint() {
        double set = 0;
//        for (LineSensor sensor : sensorList) {
//            if (sensor.getGain() == 0) {
//                set += sensor.getMinReading();
//            }
//            else {
//                set += sensor.getMaxReading();
//            }
//        }
    }

    public double getErrorAngle() {
        return -(Math.atan(getError()/yDistance));
    }


    public double getError() {
        double setpoint = 0;
        double reading = 0;
        int sensorsAtMin = 0;
        boolean[] sensors[];
        for (LineSensor sensor : sensorList) {
            System.out.println(sensor.getAnalogInput().getName() + " = " + sensor.getSensorReading());
            reading += sensor.getGain() * sensor.getSensorReading();
        }
        return error;
    }

    public double getErrorSum() {
        errorSum += error;
        return errorSum;
    }

    public void calculatePID() {
        double proportional = getKp() * getError();
        double integral = getKi() * getErrorSum();
        //double derivative = getKd() * getPreviousError();

        pidValue = proportional + integral;
    }

    public double getPidValue() {
        return pidValue;
    }
}
