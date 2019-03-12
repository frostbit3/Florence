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
package com.florence.task.impl;

import com.florence.model.player.Player;
import com.florence.model.WalkingQueue;
import com.florence.model.World;
import com.florence.task.Task;
import java.util.Iterator;

public class RestoreRunEnergyTask extends Task {

    /**
     * The modulus for determining the frequency of restoration.
     */
    public static final int RESTORATION_MODULUS = 10;

    /**
     * The amount of energy restored.
     */
    public static final int RESTORATION_INCREMENT = 1;

    public RestoreRunEnergyTask(Object source) {
        super(source);
    }

    @Override
    public void execute() {
        if (ticks % RESTORATION_MODULUS == 0) {
            Iterator<Player> iterator = World.singleton().getPlayers().iterator();
            while (iterator.hasNext()) {
                final Player player = iterator.next();
                if (player.getWalkingQueue().getEnergy() < WalkingQueue.MAXIMUM_RUN_ENERGY)
                    player.getWalkingQueue().setEnergy(
                            player.getWalkingQueue().getEnergy() + RESTORATION_INCREMENT);
            }
        }
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("Fatal error encountered. "
                + "This task is constant and should not have been cancelled.");
    }
}
