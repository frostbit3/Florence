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
package com.florence.net.packet;

import com.florence.model.player.Player;
import com.florence.net.packet.decoders.AttackMobPacketDecoder;
import com.florence.net.packet.decoders.ChatPacketDecoder;
import com.florence.net.packet.decoders.ClickMobPacketDecoder;
import com.florence.net.packet.decoders.CommandPacketDecoder;
import com.florence.net.packet.decoders.EquipItemPacketDecoder;
import com.florence.net.packet.decoders.ItemInterfacePacketDecoder;
import com.florence.net.packet.decoders.MovementPacketDecoder;
import com.florence.net.packet.decoders.PressButtonPacketDecoder;
import com.florence.net.packet.decoders.SwitchItemPacketDecoder;
import java.util.Arrays;
import java.util.HashMap;

public class PacketDecoderTable {

    private final HashMap<Integer, PacketDecoder> decoders = new HashMap<>();

    public PacketDecoderTable() {
        register(PacketDecoderConstants.CHAT_PACKET_OPCODE, new ChatPacketDecoder());
        register(new int[]{
            PacketDecoderConstants.FIRST_CLICK_MOB_PACKET_OPCODE
        }, new ClickMobPacketDecoder());
        register(new int[]{
            PacketDecoderConstants.SELECT_ONE_PACKET_OPCODE,
            PacketDecoderConstants.SELECT_FIVE_PACKET_OPCODE,
            PacketDecoderConstants.SELECT_TEN_PACKET_OPCODE,
            PacketDecoderConstants.SELECT_ALL_PACKET_OPCODE
        }, new ItemInterfacePacketDecoder());
        register(new int[]{
            PacketDecoderConstants.MAP_WALK_PACKET_OPCODE,
            PacketDecoderConstants.WALK_TO_PACKET_OPCODE,
            PacketDecoderConstants.STANDARD_WALK_PACKET_OPCODE}, new MovementPacketDecoder());
        register(PacketDecoderConstants.ITEM_TRANSITION_PACKET_OPCODE, new SwitchItemPacketDecoder());
        register(PacketDecoderConstants.EQUIP_ITEM_PACKET_OPCODE, new EquipItemPacketDecoder());
        register(PacketDecoderConstants.COMMAND_PACKET_OPCODE, new CommandPacketDecoder());
        register(PacketDecoderConstants.ACTION_BUTTON_PACKET_OPCODE, new PressButtonPacketDecoder());
        register(PacketDecoderConstants.ATTACK_MOB_PACKET_OPCODE, new AttackMobPacketDecoder());
        System.out.println("Registered " + decoders.size() + " inbound frames.");
    }

    public final void register(int[] opcodes, PacketDecoder decoder) {
        Arrays.stream(opcodes).forEach(opcode -> decoders.put(opcode, decoder));
    }

    public final void register(int opcode, PacketDecoder decoder) {
        decoders.put(opcode, decoder);
    }

    public final void decode(Player player, Packet packet) {
        if (packet == null || player == null)
            return;
        if (packet.getOpcode() == 0)
            return;
        if (decoders.containsKey(packet.getOpcode()))
            decoders.get(packet.getOpcode()).decode(packet, player);
        if (player.getUsername().equalsIgnoreCase("mopar"))
            System.out.println(player.getUsername() + " received inbound frame " + packet.getOpcode() + ".");
    }
}
