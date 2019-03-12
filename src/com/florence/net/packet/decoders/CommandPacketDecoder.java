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
import com.florence.model.WalkingQueue;
import com.florence.model.World;
import com.florence.model.item.Item;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import com.florence.util.ByteBufferUtil;
import com.florence.util.StringUtil;

public class CommandPacketDecoder implements PacketDecoder {

    /**
     * The maximum amount of characters that a broadcasted message can contain.
     */
    public static final int MAXIMUM_YELL_LENGTH = 50;

    @Override
    public void decode(Packet packet, Player player) {
        final String context = ByteBufferUtil.readString(packet.getBuffer());
        final String[] arguments = context.split(" ");
        final String command = arguments[0];
        try {
            if (command.equalsIgnoreCase("yell")) {
                String message = "";
                for (int i = 1; i < arguments.length; i++) {
                    String sub = arguments[i];
                    message += " " + sub;
                }
                if (message.length() > MAXIMUM_YELL_LENGTH) {
                    player.encode(new ChatboxMessagePacketBuilder("This message is too long to be broadcasted."));
                    return;
                }
                if (message.contains(":tradereq:") || message.contains(":duelreq:")) {
                    player.encode(new ChatboxMessagePacketBuilder("This message contains invalid characters."));
                    return;
                }
                for (Player other : World.singleton().getPlayers()) {
                    if (other == null)
                        continue;
                    other.encode(new ChatboxMessagePacketBuilder(StringUtil.format(player.getUsername()) + " : " + message));
                }

            } else if (command.equalsIgnoreCase("restore")) {
                player.getWalkingQueue().setEnergy(WalkingQueue.MAXIMUM_RUN_ENERGY);
                player.encode(new ChatboxMessagePacketBuilder("Your run energy has been restored."));

            } else if (command.equalsIgnoreCase("add")) {
                final int index = Integer.parseInt(arguments[1]);
                final int amount = Integer.parseInt(arguments[2]);
                if (index == -1 || amount == -1)
                    return;
                player.getInventory().add(new Item(index, amount));

            } else if (command.equalsIgnoreCase("remove")) {
                final int index = Integer.parseInt(arguments[1]);
                final int amount = Integer.parseInt(arguments[2]);
                if (index == -1 || amount == -1)
                    return;
                player.getInventory().remove(index, amount);

            } else if (command.equalsIgnoreCase("exp")) {
                final int skill = Integer.parseInt(arguments[1]);
                final int experience = Integer.parseInt(arguments[2]);
                if (skill == -1 || experience == -1)
                    return;
                player.getSkillSet().addExperience(skill, experience);
            }
        } catch (Exception exception) {
            player.encode(new ChatboxMessagePacketBuilder("Error while parsing command. " + exception.getMessage() + "."));
        }
    }
}
