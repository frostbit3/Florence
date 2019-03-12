/*
 * Copyright (C) 2019 Dylan Vicchiarelli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.florence.net.packet;

import com.florence.net.OutByteBuffer;
import java.nio.ByteBuffer;

public class Packet {

    /**
     * The maximum capacity of a 16 bit signed short in decimal notation.
     */
    public static final int SHORT_CAPACITY = 32767;

    private int opcode;
    private int length;
    private ByteBuffer buffer;

    public Packet(int opcode, int length, ByteBuffer buffer) {
        this.opcode = opcode;
        this.length = length;
        this.buffer = buffer;
    }

    public int readLEShortA() {
        int value = (buffer.get() - (OutByteBuffer.BITS_IN_A_BYTE * 16) & 0xFF) | ((buffer.get() & 0xFF) << OutByteBuffer.BITS_IN_A_BYTE);
        return value -= value > SHORT_CAPACITY ? 0x10000 : 0;
    }

    public int readLEShort() {
        int value = (buffer.get() & 0xFF) | ((buffer.get() & 0xFF) << OutByteBuffer.BITS_IN_A_BYTE);
        return value -= value > SHORT_CAPACITY ? 0x10000 : 0;
    }

    public int readShortA() {
        int value = ((buffer.get() & 0xFF) << OutByteBuffer.BITS_IN_A_BYTE) | (buffer.get() - (OutByteBuffer.BITS_IN_A_BYTE * 16) & 0xFF);
        return value -= value > SHORT_CAPACITY ? 0x10000 : 0;
    }

    public int readByte() {
        return buffer.get();
    }

    public int readByteA() {
        return (OutByteBuffer.BITS_IN_A_BYTE * 16) + buffer.get();
    }

    public int readByteS() {
        return (OutByteBuffer.BITS_IN_A_BYTE * 16) - buffer.get();
    }

    public byte[] readBytesA(int amount) {
        byte[] data = new byte[amount];
        for (int position = 0; position < amount; position++) {
            data[position] = (byte) (getBuffer().get() + (OutByteBuffer.BITS_IN_A_BYTE * 16));
        }
        return data;
    }

    public int readShort() {
        return buffer.getShort();
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
