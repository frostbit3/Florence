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
package com.florence.util;

import com.florence.net.OutByteBuffer;
import java.nio.ByteBuffer;

public class ByteBufferUtil {

    /**
     * Reads a sequence of bytes that compose a string.
     *
     * @param buffer The source.
     * @return The result.
     */
    public static String readString(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder();
        byte character;

        /**
         * A value of ten is used to denote an end of sequence.
         */
        while (buffer.hasRemaining() && (character = buffer.get()) != OutByteBuffer.STRING_TERMINATION) {
            builder.append((char) character);
        }
        return builder.toString();
    }
}
