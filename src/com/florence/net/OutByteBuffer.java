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
package com.florence.net;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

public class OutByteBuffer {

    /**
     * Denotes an end of sequence when writing a string.
     */
    public static final int STRING_TERMINATION = 10;

    /**
     * The maximum value of a byte.
     */
    public static final int BYTE_CAPACITY = 255;

    /**
     * The amount of values that a byte can store.
     */
    public static final int BYTE_VALUES = 256;

    /**
     * The amount of bits in a byte.
     */
    public static final int BITS_IN_A_BYTE = 8;

    /**
     * A unit of digital information in computing and telecommunications that
     * consists of eight bits.
     */
    public static final int OCTET_LENGTH = 8;

    private ByteBuffer buffer;
    private int b_position;
    private OutByteBufferAccess access = OutByteBufferAccess.BYTE_ACCESS;

    public static final int[] BIT_MASK = new int[32];

    public enum OutByteBufferAccess {

        /**
         * Messages are being written as bits.
         */
        BIT_ACCESS,
        /**
         * Messages are being written as bytes.
         */
        BYTE_ACCESS
    }

    public OutByteBuffer(int capacity) {
        /**
         * Allocates on the heap opposed to direct native memory. This should
         * never be allocated directly as that is far more expensive. Buffers
         * allocated on the heap will hold onto native memory until they get
         * automatically garbage collected. Otherwise, the native memory would
         * have to be manually pooled and released.
         */
        this(ByteBuffer.allocate(capacity));
    }

    public OutByteBuffer writeByte(int value) {
        buffer.put((byte) value);
        return this;
    }

    public OutByteBuffer writeByteC(int value) {
        buffer.put((byte) -value);
        return this;
    }

    public OutByteBuffer writeByteA(int value) {
        buffer.put((byte) (value + (BITS_IN_A_BYTE * 16)));
        return this;
    }

    public OutByteBuffer writeByteS(int value) {
        buffer.put((byte) ((BITS_IN_A_BYTE * 16) - value));
        return this;
    }

    public OutByteBuffer writeInt(int value) {
        buffer.putInt(value);
        return this;
    }

    public OutByteBuffer writeLong(long value) {
        buffer.putLong(value);
        return this;
    }

    public OutByteBuffer writeShort(int value) {
        buffer.putShort((short) value);
        return this;
    }

    public OutByteBuffer writeLEShort(int value) {
        buffer.put((byte) value);
        buffer.put((byte) (value >> BITS_IN_A_BYTE));
        return this;
    }

    public OutByteBuffer writeLEShortA(int value) {
        buffer.put((byte) (value + (BITS_IN_A_BYTE * 16)));
        buffer.put((byte) (value >> BITS_IN_A_BYTE));
        return this;
    }

    public OutByteBuffer writeBEInt(int value) {
        buffer.put((byte) (value >> (BITS_IN_A_BYTE * 2)));
        buffer.put((byte) (value >> (BITS_IN_A_BYTE * 3)));
        buffer.put((byte) value);
        buffer.put((byte) (value >> (BITS_IN_A_BYTE * 1)));
        return this;
    }

    public OutByteBuffer writeInvInteger(int value) {
        buffer.put((byte) (value >> (BITS_IN_A_BYTE * 1)));
        buffer.put((byte) (value));
        buffer.put((byte) (value >> (BITS_IN_A_BYTE * 3)));
        buffer.put((byte) (value >> (BITS_IN_A_BYTE * 2)));
        return this;
    }

    public OutByteBuffer writeShortA(int value) {
        buffer.put((byte) (value >> BITS_IN_A_BYTE));
        buffer.put((byte) (value + (BITS_IN_A_BYTE * 16)));
        return this;
    }

    public OutByteBuffer writeString(String string) {
        buffer.put(string.getBytes(StandardCharsets.US_ASCII));
        buffer.put((byte) STRING_TERMINATION);
        return this;
    }

    public OutByteBuffer writeBytes(byte[] source) {
        buffer.put(source);
        return this;
    }

    public OutByteBuffer writeBytes(ByteBuffer source) {
        IntStream.range(0, source.position()).forEach(index -> {
            writeByte(source.get(index));
        });
        return this;
    }

    public OutByteBuffer access(OutByteBufferAccess access) {
        switch (access) {

            case BIT_ACCESS:
                b_position = buffer.position() * BITS_IN_A_BYTE;
                break;

            case BYTE_ACCESS:
                buffer.position((b_position + 7) / BITS_IN_A_BYTE);
                break;
        }
        this.access = access;
        return this;
    }

    public OutByteBuffer writeBits(int amount, int value) {
        int position = b_position >> 3;
        int offset = BITS_IN_A_BYTE - (b_position & 7);
        b_position = (b_position + amount);
        int capacity = position - buffer.position() + 1;
        capacity += (amount + 7) / BITS_IN_A_BYTE;
        if (buffer.remaining() < capacity) {
            ByteBuffer old = buffer;
            buffer = ByteBuffer.allocate(old.capacity() + capacity);
            old.flip();
            buffer.put(old);
        }
        for (; amount > offset; offset = BITS_IN_A_BYTE) {
            byte tmp = buffer.get(position);
            tmp &= ~BIT_MASK[offset];
            tmp |= (value >> (amount - offset)) & BIT_MASK[offset];
            buffer.put(position++, tmp);
            amount -= offset;
        }
        if (amount == offset) {
            byte tmp = buffer.get(position);
            tmp &= ~BIT_MASK[offset];
            tmp |= value & BIT_MASK[offset];
            buffer.put(position, tmp);
        } else {
            byte tmp = buffer.get(position);
            tmp &= ~(BIT_MASK[amount] << (offset - amount));
            tmp |= (value & BIT_MASK[amount]) << (offset - amount);
            buffer.put(position, tmp);
        }
        return this;
    }

    public int position() {
        return buffer.position();
    }

    public int limit() {
        return buffer.limit();
    }

    public void flip() {
        buffer.flip();
    }

    public OutByteBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    private void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    static {
        for (int mask = 0; mask < BIT_MASK.length; mask++) {
            BIT_MASK[mask] = (1 << mask) - 1;
        }
    }
}
