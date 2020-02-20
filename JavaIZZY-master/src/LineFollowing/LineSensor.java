package LineFollowing;

import com.pi4j.io.gpio.GpioPinAnalogInput;

public class LineSensor
    {
        private int threshold;
        private GpioPinAnalogInput analogInput;

        public LineSensor(int threshold, GpioPinAnalogInput analogInput) {
            this.threshold = threshold;
            this.analogInput = analogInput;
        }

        public int getThreshold() {
            return this.threshold;
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

