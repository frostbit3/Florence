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
package com.florence.net.packet.decoders;

import com.florence.model.World;
import com.florence.model.mob.Mob;
import com.florence.model.player.Player;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;
import com.florence.net.packet.PacketDecoderConstants;
import com.florence.task.impl.WalkToMobTask;
import com.florence.task.impl.WalkToMobTask.ClickOption;

public class ClickMobPacketDecoder implements PacketDecoder {

    @Override
    public void decode(Packet packet, Player player) {
        final int opcode = packet.getOpcode();
        switch (opcode) {

            case PacketDecoderConstants.FIRST_CLICK_MOB_PACKET_OPCODE:
                final int index = packet.readLEShort();
                final Mob mob = World.singleton().getMobs().get(index);
                if (mob == null)
                    return;

                /**
                 * Sets focus.
                 */
                player.setInteractingEntity(mob);

                /**
                 * Routes this user to their target.
                 */
                World.singleton().schedule(new WalkToMobTask(player, mob, ClickOption.FIRST));
                break;
        }
    }
}
