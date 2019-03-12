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

import com.florence.model.GameConstants;
import com.florence.net.Client;
import com.florence.net.packet.Packet;
import java.nio.ByteBuffer;

public class GameMessageDecoder implements MessageDecoder {

    @Override
    public boolean decode(Client client) {
        int opcode = client.getBuffer().get() - client.getDecryption().getNextValue() & 0xFF;
        int length = GameConstants.PACKET_SIZES_317[opcode];
        if (length == -1) {
            if (client.getBuffer().hasRemaining()) {

                /**
                 * The length can't be estimated and must be manually read.
                 */
                length = client.getBuffer().get() & 0xFF;
            } else {
                System.out.println("Underflow of data while reading length for frame " + opcode + ". "
                        + "Compacting bytes.");
                client.getBuffer().compact();
            }
        }
        if (client.getBuffer().remaining() < length) {

            /**
             * There is an insufficient amount of data.
             */
            System.out.println("Underflow of data on frame " + opcode + ". Closing connection.");
            return false;
        }

        byte[] payload = new byte[length];
        client.getBuffer().get(payload);

        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(payload);
        buffer.flip();

        /**
         * Queues this packet for execution on the world's thread. Eliminates
         * synchronization overhead.
         */
        client.getPlayer().getPackets().add(new Packet(opcode, length, buffer));
        return true;
    }
}
