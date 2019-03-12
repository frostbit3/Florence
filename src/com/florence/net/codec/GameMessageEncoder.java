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
package com.florence.net.codec;

import com.florence.net.Client;
import com.florence.net.OutByteBuffer;
import com.florence.net.packet.PacketBuilder;
import com.florence.net.packet.PacketHeader;
import java.nio.ByteBuffer;

public class GameMessageEncoder implements MessageEncoder<PacketBuilder> {

    @Override
    public ByteBuffer encode(Client client, PacketBuilder builder) {
        OutByteBuffer payload = builder.build(client.getPlayer());
        payload.flip();

        final int opcode = builder.getOpcode();
        final PacketHeader header = builder.getHeader();
        int capacity = payload.limit();

        /**
         * Expands the capacity to the necessary allotment.
         */
        capacity += Byte.BYTES;
        capacity += header.equals(PacketHeader.VARIABLE_BYTE) ? Byte.BYTES
                : header.equals(PacketHeader.VARIABLE_SHORT) ? Short.BYTES : 0;

        ByteBuffer packet = ByteBuffer.allocate(capacity);
        packet.put((byte) (opcode + client.getEncryption().getNextValue()));
        switch (header) {

            case VARIABLE_BYTE:
                packet.put((byte) payload.limit());
                break;

            case VARIABLE_SHORT:
                packet.putShort((short) payload.limit());
                break;
        }

        packet.put(payload.getBuffer());
        return packet;
    }
}
