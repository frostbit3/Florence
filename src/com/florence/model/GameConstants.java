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
package com.florence.model;

import com.florence.net.OutByteBuffer;

public class GameConstants {

    public static final int STANDARD_RIGHTS = 0x0;
    public static final int MODERATOR_RIGHTS = 0x1;
    public static final int ADMINISTRATOR_RIGHTS = 0x2;

    public static final byte[] DIRECTION_DELTA_Y = new byte[]{1, 1, 1, 0, 0, -1, -1, -1};
    public static final byte[] DIRECTION_DELTA_X = new byte[]{-1, 0, 1, -1, 1, -1, 0, 1};

    public static final Position DEFAULT_POSITION = Position.create(3200, 3200, 0);
    public static final Position RESPAWN_POSITION = Position.create(3200, 3200, 0);

    /**
     * The maximum amount of a specific entity that can be in view at a time.
     * Written as eight bits, equivalent to one byte. Meaning the maximum value
     * it can hold is 255.
     */
    public static final int MAXIMUM_RENDERABLE_ENTITIES = OutByteBuffer.BYTE_CAPACITY;

    public static final int[] PACKET_SIZES_317 = {
        0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 6, 2, 2, 0, 0, 2,
        0, 6, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 4, 0, 0, 2, 2, 6, 0, 6,
        0, -1, 0, 0, 0, 0, 0, 0, 0, 12, 0, 0, 0, 0, 8, 0, 0, 8, 0, 0, 0, 0,
        0, 0, 0, 0, 6, 0, 2, 2, 8, 6, 0, -1, 0, 6, 0, 0, 0, 0, 0, 1, 4, 6,
        0, 0, 0, 0, 0, 0, 0, 3, 0, 0, -1, 0, 0, 13, 0, -1, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 1, 0, 6, 0, 0, 0, -1, 0, 2, 6, 0,
        4, 6, 8, 0, 6, 0, 0, 0, 2, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 1,
        2, 0, 2, 6, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 8, 0, 3, 0, 2, 0, 0, 8, 1, 0, 0, 12, 0, 0, 0,
        0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 4, 0, 0, 0, 7, 8, 0, 0, 10,
        0, 0, 0, 0, 0, 0, 0, -1, 0, 6, 0, 1, 0, 0, 0, 6, 0, 6, 8, 1, 0, 0, 4,
        0, 0, 0, 0, -1, 0, -1, 4, 0, 0, 6, 6, 0, 0, 0
    };
}
