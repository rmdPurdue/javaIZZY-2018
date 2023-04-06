module com.rmdPurdue.izzyRobot {
    // Module Exports
    exports com.rmdPurdue.izzyRobot;

    // Pi4J MODULES
    requires com.pi4j;
    requires com.pi4j.plugin.pigpio;
    requires com.pi4j.plugin.raspberrypi;
    requires com.pi4j.library.pigpio;
    requires com.pi4j.plugin.linuxfs;
    requires org.apache.logging.log4j;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    requires java.logging;
//    requires info.picocli;

}