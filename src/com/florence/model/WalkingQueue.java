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

import com.florence.model.player.Player;
import com.florence.net.packet.builders.ClientConfigurationPacketBuilder;
import com.florence.net.packet.builders.ConstructRegionPacketBuilder;
import com.florence.net.packet.builders.DisplayRunEnergyPacketBuilder;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Graham Edgecombe
 * @author Dylan Vicchiarelli
 *
 * Processes the movement of an {@link Entity} on the global coordinate plane.
 */
public class WalkingQueue {

    /**
     * The maximum amount of steps a path can contain.
     */
    public static final int MAXIMUM_PATH_LENGTH = 50;

    /**
     * The maximum amount of run energy a user can have.
     */
    public static final int MAXIMUM_RUN_ENERGY = 100;

    /**
     * The minimum amount of run energy a user can have.
     */
    public static final int MINIMUM_RUN_ENERGY = 0;

    /**
     * The entity.
     */
    private Entity entity;

    /**
     * The direction this entity is walking.
     */
    private Direction walkingDirection = Direction.NONE;

    /**
     * The direction this entity is running.
     */
    private Direction runningDirection = Direction.NONE;

    /**
     * Denotes if this entity is currently running.
     */
    private boolean running = false;

    /**
     * The points in the path.
     */
    private final Deque<Point> points = new ArrayDeque<>();

    /**
     * The user's current energy. Does not apply to non-player characters.
     */
    private int energy;

    public WalkingQueue(Entity entity) {
        this.entity = entity;
    }

    public void finish() {
        points.removeFirst();
    }

    public void reset() {
        points.clear();
        points.add(new Point(entity.getPosition().getX(), entity.getPosition().getY(), Direction.NONE));
    }

    public void process() {
        Point walking, running_ = null;
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.encode(new DisplayRunEnergyPacketBuilder(energy));
        }
        if (entity.hasTeleported()) {
            /**
             * Resets this entity's walking and running directions. Otherwise
             * the client may believe this entity is still in motion and become
             * out of synchronization.
             */
            walkingDirection = Direction.NONE;
            runningDirection = Direction.NONE;

            /**
             * Resets the path.
             */
            reset();
        } else {
            walking = next();
            if (running) {
                if (energy <= MINIMUM_RUN_ENERGY) {
                    if (entity instanceof Player) {
                        final Player player = (Player) entity;
                        player.encode(new ClientConfigurationPacketBuilder(
                                ClientConfigurationPacketBuilder.RUN_BUTTON_CONFIGURATION_INDEX, 0));
                        player.encode(new ChatboxMessagePacketBuilder("You don't have any energy left."));

                        /**
                         * This user can't run anymore.
                         */
                        running = false;
                    }
                } else {
                    running_ = next();
                    if (running_ != null)
                        energy -= energy > MINIMUM_RUN_ENERGY ? 1 : 0;
                }
            }

            /**
             * The direction this entity is walking, if any.
             */
            walkingDirection = (walking == null ? Direction.NONE : walking.getDirection());

            /**
             * The direction this entity is running, if any.
             */
            runningDirection = (running_ == null ? Direction.NONE : running_.getDirection());

            /**
             * Represents this entity's current viewport.
             */
            Viewport viewport = new Viewport(entity.getRegion());

            /**
             * The horizontal difference.
             */
            int deltaX = entity.getPosition().getX() - viewport.getRegionX() * 8;

            /**
             * The vertical difference.
             */
            int deltaY = entity.getPosition().getY() - viewport.getRegionY() * 8;

            if (deltaX < (2 * Viewport.SEGMENT_SIZE) || deltaX >= (11 * Viewport.SEGMENT_SIZE)
                    || deltaY < (2 * Viewport.SEGMENT_SIZE) || deltaY > (11 * Viewport.SEGMENT_SIZE)) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;

                    /**
                     * Update this user's regional viewport. Written directly to
                     * prevent a delay.
                     */
                    player.encode(new ConstructRegionPacketBuilder());
                }
            }
        }
    }

    public Point next() {
        Point point = points.poll();
        if (point == null || point.getDirection() == Direction.NONE)
            return null;
        final Position destination = Position.create(
                GameConstants.DIRECTION_DELTA_X[point.getDirection().getValue()],
                GameConstants.DIRECTION_DELTA_Y[point.getDirection().getValue()], entity.getPosition().getZ());
        entity.getPosition().add(destination);
        return point;
    }

    public void step(int x, int y) {
        if (points.isEmpty())
            reset();

        Point last = points.peekLast();
        int deltaX = x - last.getX();
        int deltaY = y - last.getY();

        int maximum = Math.max(Math.abs(deltaX), Math.abs(deltaY));
        for (int i = 0; i < maximum; i++) {
            if (deltaX < 0) {
                deltaX++;
            } else if (deltaX > 0) {
                deltaX--;
            }
            if (deltaY < 0) {
                deltaY++;
            } else if (deltaY > 0) {
                deltaY--;
            }
            if (points.size() < MAXIMUM_PATH_LENGTH)
                add(x - deltaX, y - deltaY);
        }
    }

    private void add(int x, int y) {
        Point last = points.peekLast();
        if (last == null)
            return;
        Direction direction = from(x - last.getX(), y - last.getY());
        if (direction.getValue() >= 0)
            points.add(new Point(x, y, direction));
    }

    private Direction from(int x, int y) {
        if (x < 0) {
            if (y < 0) {
                return Direction.SOUTH_WEST;
            } else if (y > 0) {
                return Direction.NORTH_WEST;
            } else {
                return Direction.WEST;
            }
        } else if (x > 0) {
            if (y < 0) {
                return Direction.SOUTH_EAST;
            } else if (y > 0) {
                return Direction.NORTH_EAST;
            } else {
                return Direction.EAST;
            }
        } else {
            if (y < 0) {
                return Direction.SOUTH;
            } else if (y > 0) {
                return Direction.NORTH;
            } else {
                return Direction.NONE;
            }
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Direction getWalkingDirection() {
        return walkingDirection;
    }

    public void setWalkingDirection(Direction walkingDirection) {
        this.walkingDirection = walkingDirection;
    }

    public Direction getRunningDirection() {
        return runningDirection;
    }

    public void setRunningDirection(Direction runningDirection) {
        this.runningDirection = runningDirection;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
