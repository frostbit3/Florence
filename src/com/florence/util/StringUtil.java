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

import java.io.DataInputStream;
import java.io.IOException;

public final class StringUtil {

    /**
     * The maximum length of a user's name.
     */
    public static final int MAXIMUM_USERNAME_LENGTH = 12;

    /**
     * A literal long value of 37.
     */
    public static final long LONG_BASE_37_LITERAL = 37L;

    /**
     * The ASCII key for an uppercase 'A'.
     */
    public static final int ASCII_LOWERCASE_A = 97;

    /**
     * The ASCII key for a lowercase 'a'.
     */
    public static final int ASCII_UPPERCASE_A = 65;

    /**
     * The ASCII key for the number zero.
     */
    public static final int ASCII_ZERO = 48;

    /**
     * Valid characters.
     */
    public static final char VALID_CHARACTERS[] = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<',
        ',', '"', '[', ']', '|', '?', '/', '`'};

    public static String decodeBase37(long decode) {
        int count = 0;
        char[] characters = new char[MAXIMUM_USERNAME_LENGTH];
        while (decode != 0L) {
            long value = decode;
            decode /= LONG_BASE_37_LITERAL;
            characters[(11 - count++)] = VALID_CHARACTERS[((int) (value - decode * 37L))];
        }
        return new String(characters, MAXIMUM_USERNAME_LENGTH - count, count);
    }

    public static long encodeBase37(String encode) {
        long value = 0L;
        int count = 0;
        do {
            char character = encode.charAt(count);
            value *= LONG_BASE_37_LITERAL;
            if ((character >= 'A') && (character <= 'Z')) {
                value += '\001' + character - ASCII_UPPERCASE_A;
            } else if ((character >= 'a') && (character <= 'z')) {
                value += '\001' + character - ASCII_LOWERCASE_A;
            } else if ((character >= '0') && (character <= '9')) {
                value += '\033' + character - ASCII_ZERO;
            }
            count++;
            if (count >= encode.length()) {
                break;
            }
        } while (count < MAXIMUM_USERNAME_LENGTH);
        while ((value % LONG_BASE_37_LITERAL == 0L) && (value != 0L)) {
            value /= LONG_BASE_37_LITERAL;
        }
        return value;
    }

    public static String format(String context) {
        String result = "";
        for (String part : context.toLowerCase().split(" ")) {
            result = result + part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
        }
        return result.trim();
    }

    public static String read(DataInputStream stream) {
        StringBuilder builder = new StringBuilder();
        byte data;
        try {
            while ((data = stream.readByte()) != 0) {
                builder.append((char) data);
            }
        } catch (IOException exception) {
            exception.printStackTrace(System.out);
        }
        return builder.toString();
    }
}
