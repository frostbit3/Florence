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

import com.florence.model.player.Player;
import com.florence.model.Position;
import com.florence.model.content.AncientTeleportSpell;
import com.florence.model.content.EmoteButtons;
import com.florence.model.content.StandardTeleportSpell;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import com.florence.net.packet.builders.LogoutPacketBuilder;

public class PressButtonPacketDecoder implements PacketDecoder {

    public static final int TOGGLE_WALK_BUTTON = 152;
    public static final int TOGGLE_RUN_BUTTON = 153;

    public static final int VARROCK_TELEPORT_BUTTON = 1164;
    public static final int LUMBRIDGE_TELEPORT_BUTTON = 1167;

    public static final int LOGOUT_BUTTON = 2458;

    @Override
    public void decode(Packet packet, Player player) {
        int button = packet.getBuffer().getShort();

        EmoteButtons.pressed(button, player);
        switch (button) {

            case LOGOUT_BUTTON:
                player.encode(new LogoutPacketBuilder());
                player.disconnect();
                break;

            case VARROCK_TELEPORT_BUTTON:
                player.teleport(new StandardTeleportSpell(Position.create(3240, 3420, 0)), true);
                break;

            case LUMBRIDGE_TELEPORT_BUTTON:
                player.teleport(new AncientTeleportSpell(Position.create(3200, 3200, 0)), true);
                break;

            case TOGGLE_WALK_BUTTON:
                player.getWalkingQueue().setRunning(false);
                break;

            case TOGGLE_RUN_BUTTON:
                player.getWalkingQueue().setRunning(true);
                break;

            default:
                player.getClient().encode(new ChatboxMessagePacketBuilder("Pressed the button " + button + "."));
                break;
        }
    }
}
