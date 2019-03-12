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
import com.florence.net.OutByteBuffer;
import com.florence.net.packet.PacketBuilder;
import com.florence.net.packet.PacketBuilderConstants;
import com.florence.net.packet.PacketHeader;

public class ItemOnInterfacePacketBuilder extends PacketBuilder {

    private final int index;
    private final int zoom;
    private final int model;

    public ItemOnInterfacePacketBuilder(int index, int zoom, int model) {
        super(PacketBuilderConstants.ITEM_ON_INTERFACE_PACKET_OPCODE, PacketHeader.FIXED);
        this.index = index;
        this.zoom = zoom;
        this.model = model;
    }

    @Override
    public OutByteBuffer build(Player player) {
        OutByteBuffer buffer = new OutByteBuffer(Short.BYTES + Short.BYTES + Short.BYTES);
        buffer.writeLEShort(index);
        buffer.writeShort(zoom);
        buffer.writeShort(model);
        return buffer;
    }
}
