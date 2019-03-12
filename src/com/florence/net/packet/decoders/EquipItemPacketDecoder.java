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
import com.florence.model.content.combat.EquipmentRequirementTable;
import com.florence.model.item.InventoryContainer;
import com.florence.model.item.Item;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;

public class EquipItemPacketDecoder implements PacketDecoder {

    @Override
    public void decode(Packet packet, Player player) {
        final int index = packet.readShort();
        final int slot = packet.readShortA();
        final int widget = packet.readShortA();

        switch (widget) {

            case InventoryContainer.INVENTORY_INTERFACE:
                final Item item = player.getInventory().getItems()[slot];
                if (item == null)
                    return;
                if (item.getIndex() != index)
                    return;
                if (!EquipmentRequirementTable.check(player, item.getIndex()))
                    return;
                player.getEquipment().equip(item.getIndex(), slot);
                break;
        }
    }
}
