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

public enum Direction {

    /**
     * None.
     */
    NONE(-1),
    /**
     * North West.
     */
    NORTH_WEST(0),
    /**
     * North.
     */
    NORTH(1),
    /**
     * North East.
     */
    NORTH_EAST(2),
    /**
     * West.
     */
    WEST(3),
    /**
     * East.
     */
    EAST(4),
    /**
     * South West.
     */
    SOUTH_WEST(5),
    /**
     * South.
     */
    SOUTH(6),
    /**
     * South East.
     */
    SOUTH_EAST(7);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
