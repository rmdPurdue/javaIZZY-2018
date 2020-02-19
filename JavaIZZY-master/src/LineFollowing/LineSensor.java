package LineFollowing;

import com.pi4j.io.gpio.GpioPinAnalogInput;

public class LineSensor
    {
//        private int pinNumber;
        private double gain;
        private int threshold;
        private GpioPinAnalogInput analogInput;

//        public LineSensor(int pinNumber) {
//            this.pinNumber = pinNumber;
//        }

        public LineSensor(int thresh) {
            this.threshold = thresh;
        }

        public double getGain() {
            return this.gain;
        }

        public void setGain(double gain)
        {
            this.gain = gain;
        }

        public int getThreshold() {
            return this.threshold;
        }

//        public int getPinNumber() {
//            return pinNumber;
//        }
//
//        public void setPinNumber(int pinNumber) {
//            this.pinNumber = pinNumber;
//        }

        public GpioPinAnalogInput getAnalogInput() {
            return this.analogInput;
        }

        public void setAnalogInput(GpioPinAnalogInput input) {
            this.analogInput = input;
        }

        public double getSensorReading() {
            return this.analogInput.getValue();
        }

        public boolean getSensorState() {
            if (getSensorReading() >= getThreshold()) {
                return false; // sensor not reading
            }
            else {
                return true; // sensor reading
            }
        }


    }

