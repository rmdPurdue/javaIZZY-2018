package ControlPanel;

import com.pi4j.io.gpio.*;

import static ControlPanel.ControlModeFlag.*;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package ControlPanel
 * @date 12/5/2018
 */
public class ButtonPanel {

    private GpioController gpio = null;
    private GpioPinDigitalInput motherModeSwitch = null;
    private GpioPinDigitalInput lineFollowingModeSwitch = null;
    private GpioPinDigitalInput rfidTrackingModeSwitch = null;
    private GpioPinDigitalInput obstacleDetectionModeSwitch = null;

    public ButtonPanel(GpioController gpio) {
        this.gpio = gpio;

        motherModeSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        lineFollowingModeSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);
        rfidTrackingModeSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        obstacleDetectionModeSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);

        motherModeSwitch.setShutdownOptions(true);
        lineFollowingModeSwitch.setShutdownOptions(true);
        rfidTrackingModeSwitch.setShutdownOptions(true);
        obstacleDetectionModeSwitch.setShutdownOptions(true);
    }

    public GpioController getGpio() {
        return gpio;
    }

    public void setGpio(GpioController gpio) {
        this.gpio = gpio;
    }

    public ControlModeFlag getMotherModeState() {
        if(motherModeSwitch.getState().isHigh()) {
            return MOTHER_MODE;
        } else {
            return null;
        }
    }

    public ControlModeFlag getLineFollowingModeState() {
        if(lineFollowingModeSwitch.getState().isHigh()) {
            return LINE_FOLLOWING;
        } else {
            return null;
        }
    }

    public ControlModeFlag getObstacleDetectionModeState() {
        if(obstacleDetectionModeSwitch.getState().isHigh()) {
            return OBSTACLE_DETECTION;
        } else {
            return null;
        }
    }

    public ControlModeFlag getRFIDTrackingModeState() {
        if(rfidTrackingModeSwitch.getState().isHigh()) {
            return RFID_TRACKING;
        } else {
            return null;
        }
    }

    /*
    *   Eventually this should be a listener on a separate thread that triggers an event
    *   to stop IZZY where she is and initiate a reset and new mode entry.
     */

}
