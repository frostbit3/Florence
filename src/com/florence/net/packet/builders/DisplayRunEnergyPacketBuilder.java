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

public class DisplayRunEnergyPacketBuilder extends PacketBuilder {

    private final int energy;

    public DisplayRunEnergyPacketBuilder(int energy) {
        super(PacketBuilderConstants.DISPLAY_RUN_ENERGY_PACKET_OPCODE, PacketHeader.FIXED);
        this.energy = energy;
    }

    @Override
    public OutByteBuffer build(Player player) {
        OutByteBuffer buffer = new OutByteBuffer(Byte.BYTES);
        buffer.writeByte(energy);
        return buffer;
    }
}
