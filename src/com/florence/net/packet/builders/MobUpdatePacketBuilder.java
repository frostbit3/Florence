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
package com.florence.net.packet.builders;

import com.florence.model.Direction;
import com.florence.model.GameConstants;
import com.florence.model.mob.Mob;
import com.florence.model.player.Player;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.Viewport;
import com.florence.model.World;
import com.florence.net.OutByteBuffer;
import com.florence.net.OutByteBuffer.OutByteBufferAccess;
import com.florence.net.packet.PacketBuilder;
import com.florence.net.packet.PacketBuilderConstants;
import com.florence.net.packet.PacketHeader;
import java.util.Iterator;

public class MobUpdatePacketBuilder extends PacketBuilder {

    /**
     * The memory allocation for the primary block. If output errors occur in
     * regards to capacity, operations should be limited per-cycle instead of
     * expanding this capacity.
     */
    public static final int MAIN_PACKET_ALLOCATION = 2048;

    /**
     * The memory allocation for the appended block.
     */
    public static final int UPDATE_STATE_ALLOCATION = 1024;

    public MobUpdatePacketBuilder() {
        super(PacketBuilderConstants.UPDATE_MOB_PACKET_OPCODE, PacketHeader.VARIABLE_SHORT);
    }

    @Override
    public OutByteBuffer build(Player player) {
        final OutByteBuffer buffer = new OutByteBuffer(MAIN_PACKET_ALLOCATION);
        final OutByteBuffer update = new OutByteBuffer(UPDATE_STATE_ALLOCATION);
        buffer.access(OutByteBuffer.OutByteBufferAccess.BIT_ACCESS);
        buffer.writeBits(OutByteBuffer.BITS_IN_A_BYTE, player.getLocalMobs().size());

        final Iterator<Mob> iterator = player.getLocalMobs().iterator();
        while (iterator.hasNext()) {
            final Mob local = iterator.next();
            if (World.singleton().getMobs().get(local.getIndex()) != null && !local.hasTeleported()
                    && local.getPosition().isWithinDistance(player.getPosition(), Viewport.MAXIMUM_RENDER_DISTANCE)) {

                /**
                 * Update the movement of this non-player character.
                 */
                updateMovement(local, buffer);

                if (local.getUpdateFlags().isUpdateRequired())

                    /**
                     * Appends any update blocks that may have been flagged by
                     * this non-player character.
                     */
                    updateState(local, update);
            } else {
                iterator.remove();
                buffer.writeBits(1, 1);
                buffer.writeBits(2, 3);
            }
        }
        for (Mob mob : World.singleton().getMobs()) {
            if (mob == null)
                continue;

            /**
             * A maximum of 255 non-player characters can be in view at one
             * time.
             */
            if (player.getLocalMobs().size() >= GameConstants.MAXIMUM_RENDERABLE_ENTITIES)
                break;

            /**
             * Out of view.
             */
            if (!player.getPosition().isWithinDistance(mob.getPosition(), Viewport.MAXIMUM_RENDER_DISTANCE))
                continue;

            /**
             * This non-player character is already rendered and has not moved
             * without walking.
             */
            if (player.getLocalMobs().contains(mob) && !mob.hasTeleported())
                continue;

            /**
             * Registers this non-player character if they aren't already
             * listed.
             */
            if (!player.getLocalMobs().contains(mob))
                player.getLocalMobs().add(mob);

            /**
             * Dispatches rendering information for this non-player character.
             */
            buffer.writeBits(14, mob.getIndex());
            buffer.writeBits(5, mob.getPosition().getY() - player.getPosition().getY());
            buffer.writeBits(5, mob.getPosition().getX() - player.getPosition().getX());
            buffer.writeBits(1, 0);
            buffer.writeBits(12, mob.getIdentity());
            buffer.writeBits(1, mob.getUpdateFlags().isUpdateRequired() ? 1 : 0);

            if (mob.getUpdateFlags().isUpdateRequired())
                updateState(mob, update);
        }
        if (update.position() > 0) {
            buffer.writeBits(14, 16383);
            buffer.access(OutByteBufferAccess.BYTE_ACCESS);
            buffer.writeBytes(update.getBuffer());
        } else {
            buffer.access(OutByteBufferAccess.BYTE_ACCESS);
        }
        return buffer;
    }

    public void updateMovement(Mob mob, OutByteBuffer buffer) {
        if (mob.getWalkingQueue().getRunningDirection() == Direction.NONE) {
            if (mob.getWalkingQueue().getWalkingDirection() == Direction.NONE) {
                if (mob.getUpdateFlags().isUpdateRequired()) {
                    buffer.writeBits(1, 1);
                    buffer.writeBits(2, 0);
                } else {
                    buffer.writeBits(1, 0);
                }
            } else {
                buffer.writeBits(1, 1);
                buffer.writeBits(2, 1);
                buffer.writeBits(3, mob.getWalkingQueue().getWalkingDirection().getValue());
                buffer.writeBits(1, mob.getUpdateFlags().isUpdateRequired() ? 1 : 0);
            }
        } else {
            buffer.writeBits(1, 1);
            buffer.writeBits(2, 2);
            buffer.writeBits(3, mob.getWalkingQueue().getWalkingDirection().getValue());
            buffer.writeBits(3, mob.getWalkingQueue().getRunningDirection().getValue());
            buffer.writeBits(1, mob.getUpdateFlags().isUpdateRequired() ? 1 : 0);
        }
    }

    public void updateState(Mob mob, OutByteBuffer update) {
        int mask = 0x0;
        if (mob.getUpdateFlags().has(UpdateFlag.HIT_UPDATE)) {
            mask |= 0x40;
        }
        if (mob.getUpdateFlags().has(UpdateFlag.ANIMATION)) {
            mask |= 0x10;
        }
        if (mob.getUpdateFlags().has(UpdateFlag.GRAPHICS)) {
            mask |= 0x80;
        }
        update.writeByte(mask);
        if (mob.getUpdateFlags().has(UpdateFlag.HIT_UPDATE)) {
            if (mob.getHit() == null)
                return;
            update.writeByteC(mob.getHit().getDamage());
            update.writeByteS(mob.getHit().getType().getValue());
            update.writeByteS(mob.getHitpoints());
            update.writeByteC(mob.getMaximumHitpoints());
        }
        if (mob.getUpdateFlags().has(UpdateFlag.ANIMATION)) {
            update.writeLEShort(mob.getAnimation().getIndex());
            update.writeByte(mob.getAnimation().getDelay());
        }
        if (mob.getUpdateFlags().has(UpdateFlag.GRAPHICS)) {
            update.writeShort(mob.getGraphic().getIndex());
            update.writeInt(mob.getGraphic().getHeight());
        }
    }
}
