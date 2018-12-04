package LineFollowing;

import com.pi4j.io.gpio.GpioPinAnalogInput;

public class LineSensor
    {
        int pinNumber;
        GpioPinAnalogInput analogInput;
        int sensorReading;
        int distance;

        public int getPinNumber() {
            return pinNumber;
        }

        public void setPinNumber(int pinNumber) {
            this.pinNumber = pinNumber;
        }

        public void setAnalogInput(GpioPinAnalogInput input) {
            analogInput = input;
        }

        public GpioPinAnalogInput getAnalogInput() {
            return analogInput;
        }

        public double getSensorReading() { return analogInput.getValue(); }

        public void setSensorReading(int sensorReading) {
            this.sensorReading = sensorReading;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance)
        {
            this.distance = distance;
        }

        public LineSensor(int pinNumber) {
            this.pinNumber = pinNumber;
        }
        public LineSensor() {

        }
    }

