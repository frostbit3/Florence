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

import com.florence.model.Entity;
import com.florence.model.Position;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.task.Task;

public class EntityCombatFollowingTask extends Task {

    public static final int DEFAULT_COMBAT_DISTANCE = 1;

    private final Entity entity;
    private final Entity other;

    public EntityCombatFollowingTask(Entity entity, Entity other) {
        super(entity);
        this.entity = entity;
        this.other = other;
    }

    @Override
    public void execute() {
        /**
         * If the combatants are within a valid distance then this method
         * requires no execution.
         */
        if (entity.getPosition().isWithinDistance(other.getPosition(), DEFAULT_COMBAT_DISTANCE))
            return;

        /**
         * Appends a directional update on every iteration. This simulates the
         * "locked on" directional facing used in combat engagements. This focal
         * point will automatically reset itself if the focus is lost.
         */
        entity.getUpdateFlags().add(UpdateFlag.FACE_ENTITY_UPDATE);

        /**
         * Finds the shortest route between two points with a Manhattan Distance
         * heuristic.
         */
        if (entity.getInteractingEntity() == null || other.getIndex() != entity.getInteractingEntity().getIndex()) {

            /**
             * Cancels the current route.
             */
            stop();
        } else {
            final int distance = DEFAULT_COMBAT_DISTANCE;

            /**
             * Prepares for traversal.
             */
            entity.getWalkingQueue().reset();

            Position step = null;
            Position[] steps = new Position[distance * 4];
            int count = 0;
            for (int direction = 0; direction < distance; direction++) {
                steps[count++] = new Position(other.getPosition().getX() - 1, other.getPosition().getY() + direction, other.getPosition().getZ());
                steps[count++] = new Position(other.getPosition().getX() + direction, other.getPosition().getY() - 1, other.getPosition().getZ());
                steps[count++] = new Position(other.getPosition().getX() + direction, other.getPosition().getY() + distance, other.getPosition().getZ());
                steps[count++] = new Position(other.getPosition().getX() + distance, other.getPosition().getY() + direction, other.getPosition().getZ());
            }
            for (Position next : steps) {
                if (next == null)
                    break;
                if (step == null || (getDifference(step) > getDifference(next)))
                    step = next;
            }
            if (step == null)
                return;
            entity.getWalkingQueue().step(step.getX(), step.getY());

            /**
             * Completes the traversal.
             */
            entity.getWalkingQueue().finish();
        }
    }

    @Override
    public void cancel() {

    }

    private double getDifference(Position current) {
        int dx = Math.abs(current.getX() - other.getPosition().getX());
        int dy = Math.abs(current.getY() - other.getPosition().getY());
        return dx + dy;
    }
}
