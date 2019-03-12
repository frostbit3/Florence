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
package com.florence.model.mob;

import com.florence.model.Entity;
import com.florence.model.World;

public class Mob extends Entity {

    /**
     * The identity number of this non-player character. Not to be confused with
     * the registration index.
     */
    private int identity;

    /**
     * The maximum amount of hit-points that this non-player character can have.
     */
    private int maximumHitpoints;

    /**
     * The amount of tiles that this non-player character occupies.
     */
    private int size;

    public Mob(int identity, int size) {
        this.identity = identity;
        this.size = size;
    }

    @Override
    public void reset() {
        flags.reset();
    }

    @Override
    public void process() {
        steps.process();
    }

    @Override
    public void add() {

        /**
         * Adds this non-player character to the world.
         */
        World.singleton().getMobs().add(this);
    }

    @Override
    public void remove() {

        /**
         * Removes this non-player character from the world.
         */
        World.singleton().getMobs().remove(index);

        /**
         * Cancels any tasks that this non-player character may have submitted.
         */
        World.singleton().getTasks().cancel(this);
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("This operation is completed elsewhere.");
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public int getMaximumHitpoints() {
        return maximumHitpoints;
    }

    public void setMaximumHitpoints(int maximumHitpoints) {
        this.maximumHitpoints = maximumHitpoints;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
