/*
 * Copyright (C) 2019 Dylan Vicchiarelli
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.florence.net.packet.decoders;

import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.player.Player;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;
import com.florence.net.packet.PacketDecoderConstants;

public class MovementPacketDecoder implements PacketDecoder {

    @Override
    public void decode(Packet packet, Player player) {
        int length = packet.getLength();
        final int opcode = packet.getOpcode();

        /**
         * Cancels the current interaction with another entity if the user has
         * clicked on either the map or the ground. Packet 98 is excluded as
         * that would nullify the target immediately when routing to an entity.
         */
        if (opcode != PacketDecoderConstants.WALK_TO_PACKET_OPCODE && player.getInteractingEntity() != null) {
            player.setInteractingEntity(null);

            /**
             * Resets the user's focused direction.
             */
            player.getUpdateFlags().add(UpdateFlag.FACE_ENTITY_UPDATE);
        }

        if (player.hasTeleported())
            return;
        if (opcode == PacketDecoderConstants.MAP_WALK_PACKET_OPCODE) {

            /**
             * Assumed to be in regards to anti-cheat software.
             */
            length -= 14;
        }

        player.getWalkingQueue().reset();

        /**
         * The amount of steps in this path.
         */
        final int steps = (length - 5) / 2;

        /**
         * The path.
         */
        final int[][] path = new int[steps][2];

        /**
         * The X coordinate.
         */
        final int x = packet.readLEShortA();

        for (int i = 0; i < steps; i++) {
            path[i][0] = packet.getBuffer().get();
            path[i][1] = packet.getBuffer().get();
        }

        /**
         * The Y coordinate.
         */
        final int y = packet.readLEShort();

        /**
         * Adds the first step.
         */
        player.getWalkingQueue().step(x, y);

        for (int i = 0; i < steps; i++) {
            path[i][0] += x;
            path[i][1] += y;

            /**
             * Fills in the remaining steps.
             */
            player.getWalkingQueue().step(path[i][0], path[i][1]);
        }

        player.getWalkingQueue().finish();
    }
}
