package util;

import java.util.EnumSet;

/**
 * @author Rich Dionne
 * @project javaIZZY-2018
 * @package util
 * @date 12/4/2018
 */
public enum ControlModeFlag {
    MOTHER_MODE,
    LINE_FOLLOWING,
    RFID_TRACKING,
    OBSTACLE_DETECTION,
    FREEWHEELING;

    public static final EnumSet<ControlModeFlag> ALL_MODES = EnumSet.allOf(ControlModeFlag.class);

}
