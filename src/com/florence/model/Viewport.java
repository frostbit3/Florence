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

/**
 * Represents a user's regional viewport.
 * @author Dylan Vicchiarelli
 */
public class Viewport {

    /**
     * The size of a segment of tiles.
     */
    public static final int SEGMENT_SIZE = 8;

    /**
     * The furthest tile distance that another player can be seen from.
     */
    public static final int MAXIMUM_RENDER_DISTANCE = SEGMENT_SIZE * 2;

    /**
     * The regional X coordinate.
     */
    private int regionX;

    /**
     * The regional Y coordinate.
     */
    private int regionY;

    /**
     * The X coordinate on the map.
     */
    private final int x;

    /**
     * The Y coordinate on the map.
     */
    private final int y;

    public Viewport(int x, int y) {
        this.x = x;
        this.y = y;
        this.regionX = (x >> 3) - 6;
        this.regionY = (y >> 3) - 6;
    }

    public Viewport(Position position) {
        this(position.getX(), position.getY());
    }

    public int getRegionX() {
        return regionX;
    }

    public int getRegionY() {
        return regionY;
    }

    public int getLocalX() {
        return x - 8 * regionX;
    }

    public int getLocalY() {
        return y - 8 * regionY;
    }
}
