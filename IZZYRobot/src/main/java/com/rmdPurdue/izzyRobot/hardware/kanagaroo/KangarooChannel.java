package com.rmdPurdue.izzyRobot.hardware.kanagaroo;//package com.rmdPurdue.Hardware.Kanagaroo;
//
//import com.rmdPurdue.Hardware.Kanagaroo.Enumerations.*;
//import com.pi4j.io.serial.*;
//import lombok.extern.log4j.Log4j2;
//
///**
// * @author Rich Dionne
// * @project JavaIZZY
// * @package com.rmdPurdue.Hardware.Kanagaroo
// * @date 12/11/2017
// */
//@Log4j2
//public class KangarooChannel {
//    private int address;
//    private char name;
//    private boolean streaming;
//    private KangarooSerial kangaroo;
//    private int commandRetryInterval;
//    public int commandTimeout;
//    private byte echoCode;
//    public KangarooMonitor monitor;
//    private int monitoredGetType;
//    private int monitoredGetFlags;
//    public KangarooStatus monitoredGetResult;
//    private byte monitoredSequenceCode;
//    private boolean monitoredSequenceCodeIsReady;
//
//    public KangarooChannel(KangarooSerial serial, char name, int address) {
//
//        if(serial == null) { throw new NullPointerException("Serial");}
//        if((int)name >= 128) { throw new IllegalArgumentException("Name");}
////        if(address < 128 || address > 255) { throw new IllegalArgumentException("Address");}
//
//        this.kangaroo = serial;
//        this.address = address;
//
//        if(name >= 'a' && name <= 'z') {
//            name = (char) (name - 'a' + 'A');
//        }
//
//        if ((name >= 'A' && name <= 'Z') || (name >= '0' && name <= '9')) {
//            this.name = name;
//        }
//
//        this.commandRetryInterval = KangarooConstants.DEFAULT_COMMAND_RETRY_INTERVAL;
//        this.commandTimeout = KangarooConstants.DEFAULT_COMMAND_TIMEOUT;
//        this.streaming = false;
//    }
//
//    public int getAddress() {
//        return address;
//    }
//
//    public int getCommandRetryInterval() {
//        return commandRetryInterval;
//    }
//
//    public void setCommandRetryInterval(int commandRetryInterval) {
//        this.commandRetryInterval = commandRetryInterval;
//    }
//
//    public int getCommandTimeout() {
//        return commandTimeout;
//    }
//
//    public void setCommandTimeout(int commandTimeout) {
//        this.commandTimeout = commandTimeout;
//    }
//
//    public char getName() {
//        return this.name;
//    }
//
//    public boolean isStreaming() {
//        return streaming;
//    }
//
//    public void setStreaming(boolean streaming) {
//        this.streaming = streaming;
//    }
//
//    public void baudRate(int baudRate){
//        int index;
//        switch (baudRate) {
//            case 9600: index = 0; break;
//            case 19200: index = 1; break;
//            case 38400: index = 2; break;
//            case 115200: index = 3; break;
//            default: throw new IllegalArgumentException("baudRate");
//        }
//        systemCommand(KangarooSystemCommand.SET_BAUD_RATE, false, index);
//    }
//
//    public KangarooStatus get(KangarooGetType type) {
//        return get(type, KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus get(KangarooGetType type, KangarooGetFlags flags) {
//        KangarooTimeout timeout = new KangarooTimeout(commandTimeout);
//
//        KangarooStatus initialStatus = getInitialSequenceCodeIfNecessary(timeout);
//        if(initialStatus != null) { return initialStatus; }
//
//        return getSpecial(type, flags, timeout);
//    }
//
//    public KangarooStatus getMax() {
//        return getMax(KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus getMax(KangarooGetFlags flags) {
//        return get(KangarooGetType.MAX, flags);
//    }
//
//    public KangarooStatus getMin() {
//        return getMin(KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus getMin(KangarooGetFlags flags) {
//        return get(KangarooGetType.MIN, flags);
//    }
//
//    public KangarooStatus getP() {
//        return getP(KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus getP(KangarooGetFlags flags) {
//        return get(KangarooGetType.P, flags);
//
//    }
//
//    public KangarooStatus getPI() {
//        return getPI(KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus getPI(KangarooGetFlags flags) {
//        return get(KangarooGetType.PI, flags);
//    }
//
//    public KangarooStatus getS() {
//        return getS(KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus getS(KangarooGetFlags flags) {
//        return get(KangarooGetType.S, flags);
//    }
//
//    public KangarooStatus getSI() {
//        return getSI(KangarooGetFlags.DEFAULT);
//    }
//
//    private KangarooStatus getSI(KangarooGetFlags flags) {
//        return get(KangarooGetType.SI, flags);
//    }
//
//    public KangarooMonitor home(){
//        KangarooCommandWriter contents = new KangarooCommandWriter();
//        return set(KangarooCommand.HOME.getValue(), contents);
//    }
//
//    public KangarooMonitor P(int position) {
//        return P(position, KangarooConstants.UNSPECIFIED_LIMIT, KangarooMoveFlags.DEFAULT);
//    }
//
//    private KangarooMonitor P(int position, int speedLimit, KangarooMoveFlags flags) {
//        return motion((byte)0x01, position, (byte)0x02, speedLimit, flags);
//    }
//
//    public KangarooMonitor PI(int position) {
//        return PI(position, KangarooConstants.UNSPECIFIED_LIMIT, KangarooMoveFlags.DEFAULT);
//    }
//
//    private KangarooMonitor PI(int positionIncrement, int speedLimit, KangarooMoveFlags flags) {
//        return motion((byte)0x41, positionIncrement, (byte)0x02, speedLimit, flags);
//    }
//
//    public KangarooError powerDown() {
//        return systemCommand(KangarooSystemCommand.POWER_DOWN, true);
//    }
//
//    public KangarooError powerDownAll() {
//        return systemCommand(KangarooSystemCommand.POWER_DOWN_ALL, true);
//
//    }
//
//    public KangarooMonitor S(int velocity) {
//        return S(velocity, KangarooConstants.UNSPECIFIED_LIMIT, KangarooMoveFlags.DEFAULT);
//    }
//
//    private KangarooMonitor S(int velocity, int rampLimit, KangarooMoveFlags flags) {
//        return motion((byte)0x02, velocity, (byte)0x03, rampLimit, flags);
//    }
//
//    public KangarooMonitor SI(int velocityIncrement) {
//        return SI(velocityIncrement, KangarooConstants.UNSPECIFIED_LIMIT, KangarooMoveFlags.DEFAULT);
//    }
//
//    private KangarooMonitor SI(int velocityIncrement, int rampLimit, KangarooMoveFlags flags) {
//        return motion((byte)0x42, velocityIncrement, (byte)0x03, rampLimit, flags);
//
//    }
//    public KangarooError serialTimeout(int millis) {
//        if(millis < 0) {
//            if(millis != -1) {
//                throw new IllegalArgumentException("Milliseconds. Timeout is negative and not -1.");
//            }
//        }
//        return systemCommand(KangarooSystemCommand.SET_SERIAL_TIMEOUT, true,
//                millis <0 ? -1 : (millis * 2 + 124) / 125);
//    }
//
//
//    public KangarooError start() {
//        KangarooCommandWriter contents = new KangarooCommandWriter();
//        //System.out.println("start command number: " + KangarooCommand.START.getValue());
//        return set(KangarooCommand.START.getValue(), contents).status().getError();
//    }
//
//    private KangarooError systemCommand(KangarooSystemCommand systemCommand, boolean expectReply, int... values) {
//        if(values == null) { throw new IllegalArgumentException("values"); }
//
//        KangarooCommandWriter contents = new KangarooCommandWriter();
//        contents.write((byte)systemCommand.getCommand());
//        for(int value : values) {
//            contents.WriteBitPackedNumber(value);
//        }
//
//        if(expectReply) {
//            return set(KangarooCommand.SYSTEM.getValue(), contents).status().getError();
//        } else {
//            setNoReply(KangarooCommand.SYSTEM.getValue(), contents);
//            return KangarooError.NONE;
//        }
//    }
//
//    public KangarooError units(int desiredUnits, int machineUnits) {
//        if(desiredUnits <= 0) { throw new IllegalArgumentException("desiredUnits"); }
//        if(machineUnits <= 0) { throw new IllegalArgumentException("machineUnits"); }
//
//        KangarooCommandWriter contents = new KangarooCommandWriter();
//        contents.WriteBitPackedNumber(desiredUnits);
//        contents.WriteBitPackedNumber(machineUnits);
//        return set(KangarooCommand.UNITS.getValue(), contents).status().getError();
//    }
//
//    KangarooMonitor set(int command, KangarooCommandWriter contents) {
//        KangarooMoveFlags moveFlags = KangarooMoveFlags.DEFAULT;
//        KangarooGetType getType = KangarooGetType.P;
//        return set(command, contents, moveFlags, getType);
//    }
//    KangarooMonitor set(int command, KangarooCommandWriter contents, KangarooMoveFlags flags) {
//        return set(command, contents, flags, KangarooGetType.P);
//    }
//
//    KangarooMonitor set(int command, KangarooCommandWriter contents, KangarooMoveFlags moveFlags, KangarooGetType getType){
//        KangarooMonitor monitor = new KangarooMonitor(this);
//
//        if(streaming) {
//            //System.out.println("Sending streaming.");
//            setNoReply(command, contents, moveFlags);
//            this.monitor = null;
//        } else {
//            log.debug("Sending not streaming.");
//            KangarooTimeout timeout = new KangarooTimeout(this.commandTimeout);
//            log.debug("Timeout: " + timeout.getTimeout());
//            moveFlags = KangarooMoveFlags.fromInteger(moveFlags.getValue() | KangarooMoveFlags.SEQUENCE_CODE.getValue());
//            log.debug("Move Flags: " + moveFlags.getValue());
//            this.monitor = monitor;
//            log.debug("Get Initial Sequence Code If Necessary...");
//            this.monitoredGetResult = getInitialSequenceCodeIfNecessary(timeout);
//
//            if(this.monitoredGetResult == null) {
//                this.monitoredGetType = getType.getValue();
//                this.monitoredGetFlags = moveFlags.getValue();
//                this.monitoredSequenceCode = nextCode(monitoredSequenceCode);
//                log.debug("Monitored Get Type = " + this.monitoredGetType);
//                log.debug("Monitored Get Flags = " + this.monitoredGetFlags);
//                log.debug("Monitored Sequence Code = " + this.monitoredSequenceCode);
//                while(!updateMonitoredResult(timeout, false)) {
//                    setNoReply(command, contents, moveFlags);
//                }
//            }
//        }
//        return monitor;
//    }
//
//    private void setNoReply(int command, KangarooCommandWriter contents) {
//        setNoReply(command, contents, KangarooMoveFlags.DEFAULT);
//    }
//
//    private void setNoReply(int command, KangarooCommandWriter contents, KangarooMoveFlags moveFlags) {
//        Serial serial = this.kangaroo.getSerial();
//
//        KangarooCommandWriter writer = new KangarooCommandWriter();
//        writer.write((byte)this.name);
//        writer.write((byte)moveFlags.getValue());
//        if( 0 != (moveFlags.getValue() & KangarooMoveFlags.SEQUENCE_CODE.getValue())) {
//            writer.write(this.monitoredSequenceCode);
//        }
//
//        writer.write(contents.getData(), contents.getOffset(), contents.getLength());
//
//        try {
//            writer.writeToStream(serial, this.address, command);
//        } catch (Exception e) {
//            try { this.kangaroo.close(); } catch (Exception ce) { }
//        }
//    }
//
//    static byte nextCode(byte code) {
//        if(++code >= 0x80) { code = 1; }
//        return code;
//    }
//
//    public boolean updateMonitoredResult(KangarooTimeout timeout, boolean acceptRepliesWithStartupSequenceCode) {
//        this.monitoredGetResult = getSpecial(KangarooGetType.fromInteger(this.monitoredGetType), KangarooGetFlags.fromInteger(this.monitoredGetFlags), timeout);
//
//        if(this.monitoredGetResult.getError().getValue() < 0) {
//            return true;
//        }
//
//        if(this.monitoredGetResult.getSequenceCode() == this.monitoredSequenceCode) {
//            return true;
//        }
//
//        if(this.monitoredGetResult.getSequenceCode() == 0 && acceptRepliesWithStartupSequenceCode) {
//            return true;
//        }
//
//        return false;
//    }
//
//    KangarooStatus getSpecial(KangarooGetType type, KangarooGetFlags flags, KangarooTimeout timeout) {
//        KangarooTimeout retry = new KangarooTimeout(this.commandRetryInterval);
//        retry.expire();
//        flags.fromInteger(flags.getValue() | KangarooGetFlags.ECHO_CODE.getValue());
//
//        while(true) {
//            if(!this.kangaroo.isOpen()) {
//                log.debug("Port is closed.");
//                return KangarooStatus.createPortNotOpen();
//            }
//            if(timeout.expired()) {
//                log.debug("Timeout has expired.");
//                return KangarooStatus.createTimedOut();
//            }
//            if(retry.expired()) {
//                log.debug("Retry has expired.");
//                retry.reset();
//                this.echoCode = nextCode(this.echoCode);
//                log.debug("Echo code: " + this.echoCode);
//                KangarooCommandWriter writer = new KangarooCommandWriter();
//                writer.write((byte)this.name);
//                writer.write((byte)flags.getValue());
//                writer.write(this.echoCode);
//                writer.write((byte)type.getValue());
//
//                try {
////                    System.out.println("Trying to write: " + KangarooCommand.STATUS.getValue());
//                    writer.writeToStream(this.kangaroo.getSerial(), this.address, KangarooCommand.STATUS.getValue());
//                } catch(Exception e) {
//                    try{ this.kangaroo.close(); } catch (Exception ce) { e.printStackTrace(); }
//                }
//            }
//
//            log.debug("Successful write. ");
//            log.debug("Waiting to receive reply.");
//            if(!this.kangaroo.tryReceivePacket(timeout.getTimeout() < retry.getTimeout() ? timeout : retry)) {
////                System.out.println("Trying to receive packet.");
//                continue;
//            }
//
//            KangarooReplyReceiver receiver = this.kangaroo.receiver;
//            if(receiver.getAddress() != this.address) { continue; }
//            if(receiver.getCommand() != KangarooReplyCode.STATUS) { continue; }
//
//            KangarooStatus result = new KangarooStatus(receiver.getBuffer(), receiver.getOffset(), receiver.getLength());
//            if(!result.isValid()) {continue;}
//            if(result.getEchoCode() != this.echoCode) {continue;}
//            return result;
//        }
//    }
//
//    private KangarooStatus getInitialSequenceCodeIfNecessary(KangarooTimeout timeout) {
//        if(this.monitoredSequenceCodeIsReady) {
//            log.debug("Monitored Sequence Code is ready: " + this.monitoredSequenceCodeIsReady);
//            return null;
//        }
//        log.debug("Monitored Sequence Code is ready (" + this.monitoredSequenceCodeIsReady + "): " + this.monitoredSequenceCode);
//        KangarooStatus status = getSpecial(KangarooGetType.P, KangarooGetFlags.SEQUENCE_CODE, timeout);
//        if(status.getError().getValue() < 0) {
//            log.error("Error: " + status.getError().getValue());
//            return status; }
//        this.monitoredSequenceCode = status.getSequenceCode();
//        this.monitoredSequenceCodeIsReady = true;
//        log.debug("Monitored Sequence Code: " + this.monitoredSequenceCode);
//        log.debug("Sequence Code is Ready: " + this.monitoredSequenceCodeIsReady);
//        return null;
//    }
//
//    KangarooMonitor motion(byte motionType, int motionValue, byte limitType, int limitValue, KangarooMoveFlags flags) {
//        KangarooCommandWriter contents = new KangarooCommandWriter();
//
//        contents.write(motionType);
//        contents.WriteBitPackedNumber(motionValue);
//
//        if(limitValue >= 0) {
//            contents.write(limitType);
//            contents.WriteBitPackedNumber(limitValue);
//        } else if(limitValue != KangarooConstants.UNSPECIFIED_LIMIT) {
//            throw new IllegalArgumentException("Limit is negative and not equal to -1.", null);
//        }
//        return set(KangarooCommand.MOVE.getValue(), contents, flags);
//    }
//
//
//}
