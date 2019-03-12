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

public class InterfaceTextPacketBuilder extends PacketBuilder {

    private final String text;
    private final int interface_;

    public InterfaceTextPacketBuilder(String text, int interface_) {
        super(PacketBuilderConstants.INTERFACE_TEXT_PACKET_OPCODE, PacketHeader.VARIABLE_SHORT);
        this.text = text;
        this.interface_ = interface_;
    }

    @Override
    public OutByteBuffer build(Player player) {
        OutByteBuffer buffer = new OutByteBuffer(text.length() + Short.BYTES + Byte.BYTES);
        buffer.writeString(text);
        buffer.writeShortA(interface_);
        return buffer;
    }
}
