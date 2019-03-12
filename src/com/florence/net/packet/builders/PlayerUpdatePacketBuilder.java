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
import com.florence.model.player.Gender;
import com.florence.model.player.Player;
import com.florence.model.player.Player.RegionState;
import com.florence.model.player.PlayerAnimations;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.Viewport;
import com.florence.model.World;
import com.florence.model.item.EquipmentContainer;
import com.florence.net.OutByteBuffer;
import com.florence.net.OutByteBuffer.OutByteBufferAccess;
import com.florence.net.packet.PacketBuilder;
import com.florence.net.packet.PacketBuilderConstants;
import com.florence.net.packet.PacketHeader;
import com.florence.util.StringUtil;
import java.util.Iterator;

public class PlayerUpdatePacketBuilder extends PacketBuilder {

    /**
     * The memory allocation for the primary block. If output errors occur in
     * regards to capacity, operations should be limited per-cycle instead of
     * expanding this capacity.
     */
    public static final int MAIN_PACKET_ALLOCATION = 8192;

    /**
     * The memory allocation for the appended block.
     */
    public static final int UPDATE_STATE_ALLOCATION = 4096;

    /**
     * The maximum amount of players that can be rendered per cycle.
     */
    public static final int MAXIMUM_RENDERS_PER_CYCLE = 20;

    public PlayerUpdatePacketBuilder() {
        super(PacketBuilderConstants.UPDATE_PLAYER_PACKET_OPCODE, PacketHeader.VARIABLE_SHORT);
    }

    @Override
    public OutByteBuffer build(Player player) {
        if (player.getRegionState().equals(RegionState.REBUILDING_REGION))
            player.encode(new ConstructRegionPacketBuilder());

        final OutByteBuffer buffer = new OutByteBuffer(MAIN_PACKET_ALLOCATION);
        final OutByteBuffer update = new OutByteBuffer(UPDATE_STATE_ALLOCATION);
        buffer.access(OutByteBufferAccess.BIT_ACCESS);

        /**
         * Updates the movement of this player.
         */
        updateMovement(buffer, player);

        if (player.getUpdateFlags().isUpdateRequired()) {

            /**
             * Appends any update blocks that may have been flagged by this
             * player.
             */
            updateState(player, update, false, true);
        }

        /**
         * The amount of other players within view.
         */
        buffer.writeBits(OutByteBuffer.BITS_IN_A_BYTE, player.getLocalPlayers().size());

        final Iterator<Player> iterator = player.getLocalPlayers().iterator();
        while (iterator.hasNext()) {
            final Player local = iterator.next();
            if (World.singleton().contains(local.getUsername())
                    && local.getPosition().isWithinDistance(player.getPosition(), Viewport.MAXIMUM_RENDER_DISTANCE)) {

                /**
                 * Updates the movement of other players.
                 */
                updateLocalMovement(buffer, local);
                if (local.getUpdateFlags().isUpdateRequired()) {

                    /**
                     * Appends any update blocks that may have been flagged by
                     * other players.
                     */
                    updateState(local, update, false, false);
                }
            } else {
                iterator.remove();
                buffer.writeBits(1, 1);
                buffer.writeBits(2, 3);
            }
        }

        /**
         * The amount of players that were added into view during this cycle.
         */
        int p_count = 0;

        for (Player other : World.singleton().getPlayers()) {
            if (other == null || other.getIndex() == -1)
                continue;
            /**
             * A maximum of 255 players can be in view at one time and a maximum
             * of 20 players can be added into view per cycle.
             */
            if (p_count > MAXIMUM_RENDERS_PER_CYCLE || player.getLocalPlayers().size() >= GameConstants.MAXIMUM_RENDERABLE_ENTITIES) {
                break;
            }
            if (other == player || player.getLocalPlayers().contains(other))
                continue;
            if (other.getPosition().isWithinDistance(player.getPosition(), Viewport.MAXIMUM_RENDER_DISTANCE)) {
                player.getLocalPlayers().add(other);
                buffer.writeBits(11, other.getIndex());
                buffer.writeBits(1, 1);
                buffer.writeBits(1, 1);
                buffer.writeBits(5, other.getPosition().getY() - player.getPosition().getY());
                buffer.writeBits(5, other.getPosition().getX() - player.getPosition().getX());
                updateState(other, update, true, false);
                p_count++;
            }
        }
        if (update.position() > 0) {
            buffer.writeBits(0xB, 0x7FF);
            buffer.access(OutByteBufferAccess.BYTE_ACCESS);
            buffer.writeBytes(update.getBuffer());
        } else {
            buffer.access(OutByteBufferAccess.BYTE_ACCESS);
        }
        return buffer;
    }

    public void updateState(Player player, OutByteBuffer update, boolean forced, boolean chat) {
        int mask = 0x0;
        if (player.getUpdateFlags().has(UpdateFlag.APPEARANCE) || forced) {
            mask |= 0x10;
        }
        if (player.getUpdateFlags().has(UpdateFlag.ANIMATION)) {
            mask |= 0x8;
        }
        if (player.getUpdateFlags().has(UpdateFlag.CHAT) && !chat) {
            mask |= 0x80;
        }
        if (player.getUpdateFlags().has(UpdateFlag.FACE_ENTITY_UPDATE)) {
            mask |= 0x1;
        }
        if (player.getUpdateFlags().has(UpdateFlag.GRAPHICS)) {
            mask |= 0x100;
        }
        if (mask >= 0x100) {
            mask |= 0x40;
            update.writeByte(mask & 0xFF);
            update.writeByte(mask >> 8);
        } else {
            update.writeByte(mask);
        }
        if (player.getUpdateFlags().has(UpdateFlag.GRAPHICS)) {
            update.writeLEShort(player.getGraphic().getIndex());
            update.writeInt(player.getGraphic().getHeight());
        }
        if (player.getUpdateFlags().has(UpdateFlag.ANIMATION)) {
            update.writeLEShort(player.getAnimation().getIndex());
            update.writeByteC(player.getAnimation().getDelay());
        }
        if (player.getUpdateFlags().has(UpdateFlag.CHAT) && !chat) {
            update.writeLEShort(((player.getChatColor() & 0xFF) << OutByteBuffer.BITS_IN_A_BYTE) | (player.getChatEffects() & 0xFF));
            update.writeByte((byte) player.getRights());
            update.writeByteC(player.getChat().length);
            update.writeBytes(player.getChat());
        }
        if (player.getUpdateFlags().has(UpdateFlag.FACE_ENTITY_UPDATE)) {
            if (player.getInteractingEntity() == null) {
                update.writeLEShort(0);
            } else {
                update.writeLEShort(player.getInteractingEntity().getIndex());
            }
        }
        if (player.getUpdateFlags().has(UpdateFlag.APPEARANCE) || forced) {
            OutByteBuffer properties = new OutByteBuffer(128);
            properties.writeByte(player.getAppearance().getGender().getValue());
            properties.writeByte(0);
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HEAD] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HEAD].getIndex());
            } else {
                properties.writeByte(0);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CAPE] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CAPE].getIndex());
            } else {
                properties.writeByte(0);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_AMULET] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_AMULET].getIndex());
            } else {
                properties.writeByte(0);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_WEAPON] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_WEAPON].getIndex());
            } else {
                properties.writeByte(0);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CHEST] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CHEST].getIndex());
            } else {
                properties.writeShort(0x100 + player.getAppearance().getValues()[0]);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_SHIELD] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_SHIELD].getIndex());
            } else {
                properties.writeByte(0);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CHEST] != null) {
                if (!EquipmentContainer.FULL_BODY_GEAR.contains(player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CHEST].getIndex())) {
                    properties.writeShort(0x100 + player.getAppearance().getValues()[1]);
                } else {
                    properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_CHEST].getIndex());
                }
            } else {
                properties.writeShort((0x100 + player.getAppearance().getValues()[1]));
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_LEGS] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_LEGS].getIndex());
            } else {
                properties.writeShort(0x100 + player.getAppearance().getValues()[2]);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HEAD] != null) {
                if (!EquipmentContainer.FULL_HEAD_GEAR.contains(player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HEAD].getIndex())) {
                    properties.writeShort(0x100 + player.getAppearance().getValues()[3]);
                } else {
                    properties.writeByte(0);
                }
            } else {
                properties.writeShort(0x100 + player.getAppearance().getValues()[3]);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HANDS] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HANDS].getIndex());
            } else {
                properties.writeShort(0x100 + player.getAppearance().getValues()[4]);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_FEET] != null) {
                properties.writeShort(0x200 + player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_FEET].getIndex());
            } else {
                properties.writeShort(0x100 + player.getAppearance().getValues()[5]);
            }
            if (player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HEAD] != null) {
                if (!EquipmentContainer.FULL_HEAD_GEAR.contains(player.getEquipment().getItems()[EquipmentContainer.EQUIPMENT_SLOT_HEAD].getIndex())) {
                    properties.writeShort(0x100 + player.getAppearance().getValues()[6]);
                } else {
                    properties.writeByte(0);
                }
            } else {
                if (player.getAppearance().getGender() == Gender.MALE) {
                    properties.writeShort(0x100 + player.getAppearance().getValues()[6]);
                } else {
                    properties.writeByte(0);
                }
            }
            properties.writeByte(player.getAppearance().getColors()[0]);
            properties.writeByte(player.getAppearance().getColors()[1]);
            properties.writeByte(player.getAppearance().getColors()[2]);
            properties.writeByte(player.getAppearance().getColors()[3]);
            properties.writeByte(player.getAppearance().getColors()[4]);
            properties.writeShort(player.getAnimations().getStanding().getIndex());
            properties.writeShort(PlayerAnimations.DEFAULT_TURNING.getIndex());
            properties.writeShort(player.getAnimations().getWalking().getIndex());
            properties.writeShort(PlayerAnimations.DEFAULT_BACKTRACK.getIndex());
            properties.writeShort(PlayerAnimations.DEFAULT_SIDESTEP_A.getIndex());
            properties.writeShort(PlayerAnimations.DEFAULT_SIDESTEP_B.getIndex());
            properties.writeShort(player.getAnimations().getRunning().getIndex());
            properties.writeLong(StringUtil.encodeBase37(player.getUsername()));
            properties.writeByte((int) player.getSkillSet().getCombatLevel());
            properties.writeShort(0);
            update.writeByteC(properties.getBuffer().position());
            update.writeBytes(properties.getBuffer());
        }
    }

    public void updateMovement(OutByteBuffer buffer, Player player) {
        Viewport viewport = new Viewport(player.getRegion());
        if (player.getRegionState().equals(RegionState.REBUILDING_REGION)) {
            buffer.writeBits(1, 1);
            buffer.writeBits(2, 3);
            buffer.writeBits(2, player.getPosition().getZ());
            buffer.writeBits(1, 1);
            buffer.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
            buffer.writeBits(7, viewport.getLocalY());
            buffer.writeBits(7, viewport.getLocalX());
        } else {
            if (player.getWalkingQueue().getWalkingDirection() == Direction.NONE) {
                if (player.getUpdateFlags().isUpdateRequired()) {
                    buffer.writeBits(1, 1);
                    buffer.writeBits(2, 0);
                } else {
                    buffer.writeBits(1, 0);
                }
            } else {
                if (player.getWalkingQueue().getRunningDirection() == Direction.NONE) {
                    buffer.writeBits(1, 1);
                    buffer.writeBits(2, 1);
                    buffer.writeBits(3, player.getWalkingQueue().getWalkingDirection().getValue());
                    buffer.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
                } else {
                    buffer.writeBits(1, 1);
                    buffer.writeBits(2, 2);
                    buffer.writeBits(3, player.getWalkingQueue().getWalkingDirection().getValue());
                    buffer.writeBits(3, player.getWalkingQueue().getRunningDirection().getValue());
                    buffer.writeBits(1, player.getUpdateFlags().isUpdateRequired() ? 1 : 0);
                }
            }
        }
    }

    public void updateLocalMovement(OutByteBuffer buffer, Player other) {
        if (other.getWalkingQueue().getWalkingDirection() == Direction.NONE) {
            if (other.getUpdateFlags().isUpdateRequired()) {
                buffer.writeBits(1, 1);
                buffer.writeBits(2, 0);
            } else {
                buffer.writeBits(1, 0);
            }
        } else {
            if (other.getWalkingQueue().getRunningDirection() == Direction.NONE) {
                buffer.writeBits(1, 1);
                buffer.writeBits(2, 1);
                buffer.writeBits(3, other.getWalkingQueue().getWalkingDirection().getValue());
                buffer.writeBits(1, other.getUpdateFlags().isUpdateRequired() ? 1 : 0);
            } else {
                buffer.writeBits(1, 1);
                buffer.writeBits(2, 2);
                buffer.writeBits(3, other.getWalkingQueue().getWalkingDirection().getValue());
                buffer.writeBits(3, other.getWalkingQueue().getRunningDirection().getValue());
                buffer.writeBits(1, other.getUpdateFlags().isUpdateRequired() ? 1 : 0);
            }
        }
    }
}
