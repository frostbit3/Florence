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

import com.florence.model.UpdateFlags;
import com.florence.model.WalkToActions;
import com.florence.model.mob.Mob;
import com.florence.model.player.Player;
import com.florence.task.Task;

public class WalkToMobTask extends Task {

    /**
     * The default size.
     */
    public static final int DEFAULT_MOB_SIZE = 1;

    /**
     * The maximum amount of ticks that the user has to arrive at the mob's
     * position.
     */
    public static final int MAXIMUM_ROUTING_TIME = 30;

    /**
     * The user.
     */
    private final Player player;

    /**
     * The target.
     */
    private final Mob mob;

    /**
     * Denotes which option the user clicked on.
     */
    private final ClickOption option;

    public enum ClickOption {

        /**
         * The first option.
         */
        FIRST,
        /**
         * The second option.
         */
        SECOND,
        /**
         * The third option.
         */
        THIRD
    }

    public WalkToMobTask(Player player, Mob mob, ClickOption option) {
        super(player);
        this.player = player;
        this.mob = mob;
        this.option = option;
    }

    @Override
    public void execute() {
        if (player.getInteractingEntity() == null || ticks == MAXIMUM_ROUTING_TIME || mob.isDead()) {
            stop();
        } else {
            /**
             * Continuously puts directional focus onto the target.
             */
            player.getUpdateFlags().add(UpdateFlags.UpdateFlag.FACE_ENTITY_UPDATE);

            /**
             * The difference between the two positions is acceptable.
             */
            if (player.getPosition().isWithinDistance(mob.getPosition(), mob.getSize())) {
                switch (option) {

                    case FIRST:
                        WalkToActions.firstOptionMob(mob, player);
                        break;
                }

                /**
                 * No further processing is required.
                 */
                stop();
            }
        }

    }

    @Override
    public void cancel() {

    }
}
