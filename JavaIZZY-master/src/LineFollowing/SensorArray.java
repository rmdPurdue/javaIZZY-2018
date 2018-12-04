package LineFollowing;

import java.util.ArrayList;
import java.util.Iterator;

public class SensorArray
{
    double errorSum;
    double lastError;
    float kp;
    float ki;
    float kd;
    float error = 0;
    ArrayList<LineSensor> sensorList = new ArrayList<>();

    public SensorArray(){
        errorSum = 0;
        lastError = 0;

        kp = 2;
        ki = (float) .5;
        kd = (float) .7;
        error = 0;
    }

    public void addSensor(LineSensor sensor){
        sensorList.add(sensor);
    }
    public LineSensor getSensor(int index){
        return sensorList.get(index);
    }

    public double getKp() {
        return kp;
    }

    public void setKp(float kp) {
        this.kp = kp;
    }

    public double getKi() {
        return ki;
    }

    public void setKi(float ki) {
        this.ki = ki;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(float kd) {
        this.kd = kd;
    }


    public void getError() {
        float numerator = 0;
        float denominator = 0;

        Iterator itr = sensorList.iterator();

        while(itr.hasNext()){
            LineSensor sensor = (LineSensor)itr.next();
            //System.out.println(sensor.getAnalogInput().getName() + " = " + sensor.getSensorReading());
            numerator += sensor.getDistance() * sensor.getSensorReading();
            denominator += sensor.getSensorReading();
            error =  numerator/denominator;
        }
    }


    public double geterrorSum() {
        errorSum = error + errorSum;
        return errorSum;
    }

    public double getlastError() {
        return lastError = error;
    }

    public double pidValue()
    {
        getError();
        double integral = ki * geterrorSum();
        double proportional = kp * error;
        double derivative = kd * (error - lastError);
        lastError = error;

      return integral + proportional + derivative;
    }
}
