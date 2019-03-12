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

import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.player.Player;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;

public class ChatPacketDecoder implements PacketDecoder {

    @Override
    public void decode(Packet packet, Player player) {
        final int effects = packet.readByteS();
        final int color = packet.readByteS();
        final int length = (packet.getLength() - 2);
        final byte[] message = packet.readBytesA(length);

        player.setChatEffects(effects);
        player.setChatColor(color);
        player.setChat(message);

        player.getUpdateFlags().add(UpdateFlag.CHAT);
    }
}
