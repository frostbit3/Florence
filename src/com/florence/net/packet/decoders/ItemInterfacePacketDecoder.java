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
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.item.EquipmentContainer;
import com.florence.model.item.Item;
import com.florence.model.item.ItemDefinitions;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;
import com.florence.net.packet.PacketDecoderConstants;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;

public class ItemInterfacePacketDecoder implements PacketDecoder {

    @Override
    public void decode(Packet packet, Player player) {
        final int opcode = packet.getOpcode();
        switch (opcode) {

            case PacketDecoderConstants.SELECT_ONE_PACKET_OPCODE:
                final int widget = packet.readShortA() & 0xFFFF;
                final int slot = packet.readShortA() & 0xFFFF;
                final int index = packet.readShortA() & 0xFFFF;

                switch (widget) {

                    case EquipmentContainer.EQUIPMENT_INTERFACE:
                        final Item item = player.getEquipment().getItems()[slot];
                        if (item == null)
                            return;
                        if (item.getIndex() != index)
                            return;
                        if (player.getInventory().available() == 0
                                && !(ItemDefinitions.getDefinitions()[index].isStackable() && player.getInventory().contains(index))) {
                            player.encode(new ChatboxMessagePacketBuilder("You don't have the required inventory space to remove this item."));
                            return;
                        }
                        player.getInventory().add(new Item(index, item.getAmount()));
                        player.getEquipment().unslot(slot, item.getAmount());
                        player.getEquipment().refresh();

                        if (slot != EquipmentContainer.EQUIPMENT_SLOT_ARROWS && slot != EquipmentContainer.EQUIPMENT_SLOT_RING)
                            player.getUpdateFlags().add(UpdateFlag.APPEARANCE);
                        break;
                }
                break;
        }
    }
}
