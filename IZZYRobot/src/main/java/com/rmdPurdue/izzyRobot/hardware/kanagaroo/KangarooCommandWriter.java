package com.rmdPurdue.izzyRobot.hardware.kanagaroo;//package com.rmdPurdue.Hardware.Kanagaroo;
//
//import com.pi4j.io.serial.Serial;
//
//import java.io.IOException;
//
///**
// * @author Rich Dionne
// * @project JavaIZZY
// * @package com.rmdPurdue.Hardware.Kanagaroo
// * @date 12/11/2017
// */
//public class KangarooCommandWriter {
//
//    private byte[] data = new byte[KangarooConstants.COMMAND_MAX_BUFFER_LENGTH];
//    private int length;
//    private int offset = 0;
//    private byte[] buffer;
//
//    public KangarooCommandWriter() {
//
//    }
//
//    public byte[] getData() {
//        return data;
//    }
//
//    public int getLength() {
//        return length;
//    }
//
//    public int getOffset() {
//        return offset;
//    }
//
//    public void write(byte data) {
//        this.data[this.length++] = data;
//    }
//
//    public void write(byte[] data, int offset, int count) {
//        for(int i = 0; i < count; i++) {
//            write(data[offset + 1]);
//        }
//    }
//
//    public void WriteBitPackedNumber(int number) {
//        number = Math.max(-KangarooConstants.BIT_PACKED_MAX_VALUE, Math.min(KangarooConstants.BIT_PACKED_MAX_VALUE, number));
//        int encodedNumber;
//        if(number < 0) {
//            encodedNumber = -number;
//            encodedNumber <<= 1;
//            encodedNumber |= 1;
//        } else {
//            encodedNumber = number;
//            encodedNumber <<= 1;
//        }
//
//        while(encodedNumber !=0) {
//            write((byte)((byte)(encodedNumber & 0x3f) | (byte)(encodedNumber >= 0x40 ? 0x40 : 0x00)));
//            encodedNumber >>= 6;
//        }
//    }
//
//    public int writeToBuffer(byte[] buffer, int offset, int address, int command) {
//        return writeToBuffer(buffer, offset, address, command, this.data, offset, this.length);
//    }
//
//    private int writeToBuffer(byte[] buffer, int offset, int address, int command, byte[] data, int offsetOfData, int lengthOfData) {
//        int i = offset;
//        //System.out.println(address);
//        this.buffer[i++] = (byte)( address & 0xFF);
//        //System.out.println(this.buffer[0]);
//        this.buffer[i++] = (byte)command;
//        this.buffer[i++] = (byte)lengthOfData;
//        for(byte j = 0; j < lengthOfData; j++) {
//            this.buffer[i++] = data[offsetOfData + j];
//        }
//
//        int crc = KangarooCRC.value(buffer, 0, i - offset);
//        this.buffer[i++] = (byte)(crc & 0x7F);
//        this.buffer[i++] = (byte)((crc >> 7) & 0x7F);
//
//        return i - offset;
//    }
//
//    public void writeToStream(Serial serial, int address, int command) {
//        writeToStream(serial, address, command, this.data, offset, this.length);
//    }
//
//    private void writeToStream(Serial serial, int address, int command, byte[] data, int offsetOfData, int lengthOfData) {
//        this.buffer = new byte[KangarooConstants.COMMAND_MAX_BUFFER_LENGTH];
//        int lengthOfBuffer = writeToBuffer(this.buffer, 0, address, command, data, offsetOfData, lengthOfData);
////        System.out.println("Address: " + address + " Address in buffer: " + buffer[0]);
//        for(int i = 0; i < lengthOfBuffer; i ++){
//            try {
//                //System.out.println("buffer[" + i + "]: " + this.buffer[i]);
//                serial.write(this.buffer[i]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
////        serial.write(buffer, 0, lengthOfBuffer);
//    }
//
//}
