package MotherCommunication.LineFollowing.Devices;

public class IZZY {

    private int speed;
    private double pidValue;
    private double errorAngle;
    private double kp;
    private double ki;
    private double kd;
    private boolean isMoving;
    private boolean sensor0;
    private boolean sensor1;
    private boolean sensor2;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getPidValue() {
        return pidValue;
    }

    public void setPidValue(double pidValue) {
        this.pidValue = pidValue;
    }

    public double getErrorAngle() {
        return errorAngle;
    }

    public void setErrorAngle(double errorAngle) {
        this.errorAngle = errorAngle;
    }

    public double getKp() {
        return kp;
    }

    public void setKp(double kp) {
        this.kp = kp;
    }

    public double getKi() {
        return ki;
    }

    public void setKi(double ki) {
        this.ki = ki;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean isSensor0() {
        return sensor0;
    }

    public void setSensor0(boolean sensor0) {
        this.sensor0 = sensor0;
    }

    public boolean isSensor1() {
        return sensor1;
    }

    public void setSensor1(boolean sensor1) {
        this.sensor1 = sensor1;
    }

    public boolean isSensor2() {
        return sensor2;
    }

    public void setSensor2(boolean sensor2) {
        this.sensor2 = sensor2;
    }

    @Override
    public String toString() {
        return "IZZY{" +
                "speed=" + speed +
                ", pidValue=" + pidValue +
                ", errorAngle=" + errorAngle +
                ", kp=" + kp +
                ", ki=" + ki +
                ", kd=" + kd +
                ", isMoving=" + isMoving +
                ", sensor0=" + sensor0 +
                ", sensor1=" + sensor1 +
                ", sensor2=" + sensor2 +
                '}';
    }
}
