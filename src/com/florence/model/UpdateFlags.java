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

import java.util.LinkedList;
import java.util.List;

public class UpdateFlags {

    public enum UpdateFlag {

        /**
         * Signals a chat update block.
         */
        CHAT,
        /**
         * Signals a hit update block.
         */
        HIT_UPDATE,
        /**
         * Signals a directional update block.
         */
        FACE_ENTITY_UPDATE,
        /**
         * Signals an appearance update block.
         */
        APPEARANCE,
        /**
         * Signals a graphical update block.
         */
        GRAPHICS,
        /**
         * Signals an animation update block.
         */
        ANIMATION
    }

    /**
     * An ordered collection of flags that denote specific update blocks.
     */
    private final List<UpdateFlag> flags = new LinkedList<>();

    /**
     * Adds a new flag to the collection.
     * @param flag The flag to be added.
     */
    public void add(UpdateFlag flag) {
        flags.add(flag);
    }

    /**
     * Determines if a specific flag is contained in the collection.
     * @param flag The flag.
     * @return The result.
     */
    public boolean has(UpdateFlag flag) {
        return flags.contains(flag);
    }

    /**
     * Clears the collection.
     */
    public void reset() {
        flags.clear();
    }

    /**
     * Determines if an update is required. If a single flag is contained then
     * an update must be serviced.
     * @return The result.
     */
    public boolean isUpdateRequired() {
        return !flags.isEmpty();
    }
}
