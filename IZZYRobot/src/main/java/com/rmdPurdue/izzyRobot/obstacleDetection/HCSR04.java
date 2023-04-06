package com.rmdPurdue.izzyRobot.obstacleDetection;//package com.rmdPurdue.ObstacleDetection;
//
//import com.pi4j.io.gpio.GpioPinDigitalInput;
//import com.pi4j.io.gpio.GpioPinDigitalOutput;
//
///**
// * @author Rich Dionne
// * @project JavaIZZY
// * @package PACKAGE_NAME
// * @date 12/9/2017
// */
//public class HCSR04 {
//
//    private GpioPinDigitalOutput triggerPin;
//    private GpioPinDigitalInput echoPin;
//    private static final int SPEED_OF_SOUND = 343000; // microns per ms
////    private static final long BILLION = (long) 10E9;
////    private static final int TEN_MICRO_SEC = 10000; // nano seconds
////    private static final Format DF22 = new DecimalFormat("#0.00");
//
//    public HCSR04() {
//    }
//
//    public HCSR04(GpioPinDigitalInput echoPin) {
//        this.echoPin = echoPin;
//    }
//
//    public HCSR04(GpioPinDigitalOutput triggerPin, GpioPinDigitalInput echoPin) {
//        this.triggerPin = triggerPin;
//        this.echoPin = echoPin;
//    }
//
//    public GpioPinDigitalOutput getTriggerPin() {
//        return triggerPin;
//    }
//
//    public void setTriggerPin(GpioPinDigitalOutput triggerPin) {
//        this.triggerPin = triggerPin;
//    }
//
//    public GpioPinDigitalInput getEchoPin() {
//        return echoPin;
//    }
//
//    public void setEchoPin(GpioPinDigitalInput echoPin) {
//        this.echoPin = echoPin;
//    }
//
//    public boolean isReadyToListen() {
//        return echoPin.isLow();
//    }
//
//    public long getTimeOfFlight() {
//        long pulseStart;
//        long pulseEnd;
//        long pulseDuration;
//
//        fireTrigger();
//
//        while(echoPin.isLow()) { }
//        pulseStart = System.nanoTime();
//
//        while(echoPin.isHigh()) { }
//        pulseEnd = System.nanoTime();
//
//        pulseDuration = pulseEnd - pulseStart;
//
//        return pulseDuration;
//    }
//
//    public void fireTrigger() {
//        triggerPin.low();
//        waitMicros(200);
//        triggerPin.high();
//        waitMicros(10);
//        triggerPin.low();
//    }
//
//    public long getDistance() {
//        long distance =0;
//        long pulseStart = 0;
//        long pulseEnd = 0;
//        long pulseDuration;
//
//        triggerPin.low();
//        waitMicros(200);
//        triggerPin.high();
//        waitMicros(10);
//        triggerPin.low();
//
//        while(echoPin.isLow()) { }
//        pulseStart = System.currentTimeMillis();
//
//        while(echoPin.isHigh()) { }
//        pulseEnd = System.currentTimeMillis();
//
//        pulseDuration = pulseEnd - pulseStart;
//
//        distance = (pulseDuration / 2) * SPEED_OF_SOUND;
//
//        return distance;
//    }
//
//    public void waitMicros(long micros) {
//        long waitUntil = System.nanoTime() + (micros * 1000);
//        while(waitUntil > System.nanoTime()) {
//            // Do nothing; waiting.
//        }
//    }
//}
