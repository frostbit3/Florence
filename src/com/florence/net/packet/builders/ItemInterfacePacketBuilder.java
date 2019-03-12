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

import com.florence.model.player.Player;
import com.florence.model.item.Item;
import com.florence.net.OutByteBuffer;
import com.florence.net.packet.PacketBuilder;
import com.florence.net.packet.PacketBuilderConstants;
import com.florence.net.packet.PacketHeader;

public class ItemInterfacePacketBuilder extends PacketBuilder {

    private final int index;
    private final Item[] items;

    public ItemInterfacePacketBuilder(int index, Item[] items) {
        super(PacketBuilderConstants.ITEM_INTERFACE_PACKET_OPCODE, PacketHeader.VARIABLE_SHORT);
        this.index = index;
        this.items = items;
    }

    @Override
    public OutByteBuffer build(Player player) {
        OutByteBuffer buffer = new OutByteBuffer(10 + (items.length * 10) + (items.length * 10));
        buffer.writeShort(index);
        buffer.writeShort(items.length);
        for (Item item : items) {
            if (item != null) {
                if (item.getAmount() >= OutByteBuffer.BYTE_CAPACITY) {
                    buffer.writeByte(OutByteBuffer.BYTE_CAPACITY);
                    buffer.writeBEInt(item.getAmount());
                } else {
                    buffer.writeByte(item.getAmount());
                }
                buffer.writeLEShortA(item.getIndex() + 1);
            } else {
                buffer.writeByte(0);
                buffer.writeLEShortA(0);
            }
        }
        return buffer;
    }
}
