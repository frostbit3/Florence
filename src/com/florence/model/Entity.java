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

import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.content.TeleportSpell;
import com.florence.model.content.combat.CombatHit;

public abstract class Entity {

    protected int index;
    private int hitpoints;
    private boolean dead;
    protected final Position position = new Position(0, 0, 0);
    protected final Position region = new Position(0, 0, 0);

    private Animation animation;
    private Graphic graphic;
    private CombatHit hit;
    protected final UpdateFlags flags = new UpdateFlags();
    protected final WalkingQueue steps = new WalkingQueue(this);

    /**
     * Another entity that is currently being interacted with.
     */
    private Entity interactingEntity;

    /**
     * Denotes if this entity has moved without walking.
     */
    private boolean teleported;

    /**
     * Resets this entity's update states.
     */
    public abstract void reset();

    /**
     * Processes logic pertinent to the update procedure.
     */
    public abstract void process();

    /**
     * Adds this entity to the world.
     */
    public abstract void add();

    /**
     * Removes this entity from the world.
     */
    public abstract void remove();

    /**
     * Updates this entity.
     */
    public abstract void update();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position_) {
        position.set(position_);
    }

    public boolean hasTeleported() {
        return teleported;
    }

    public void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    public Position getRegion() {
        return region;
    }

    public void setRegion(Position region_) {
        region.set(region_);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {

        /**
         * Append an animation update on the next iteration.
         */
        flags.add(UpdateFlag.ANIMATION);

        /**
         * Sets the value.
         */
        this.animation = animation;
    }

    public Graphic getGraphic() {
        return graphic;
    }

    public void setGraphic(Graphic graphic) {

        /**
         * Append a graphical update on the next iteration.
         */
        flags.add(UpdateFlag.GRAPHICS);

        /**
         * Sets the value.
         */
        this.graphic = graphic;
    }

    public UpdateFlags getUpdateFlags() {
        return flags;
    }

    public WalkingQueue getWalkingQueue() {
        return steps;
    }

    public void teleport(TeleportSpell teleport) {

        /**
         * Cancels any current interaction.
         */
        interactingEntity = null;

        /**
         * Schedules the timed logic.
         */
        World.singleton().schedule(teleport.cast(this));
    }

    public Entity getInteractingEntity() {
        return interactingEntity;
    }

    public void setInteractingEntity(Entity interactingEntity) {
        this.interactingEntity = interactingEntity;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public CombatHit getHit() {
        return hit;
    }

    public void setHit(CombatHit hit) {
        this.hit = hit;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
