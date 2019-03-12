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

public class GameframeWidgetPacketBuilder extends PacketBuilder {

    public static final int ATTACK_WIDGET_INTERFACE = 2423;
    public static final int SKILL_WIDGET_INTERFACE = 3917;
    public static final int QUEST_WIDGET_INTERFACE = 638;
    public static final int INVENTORY_WIDGET_INTERFACE = 3213;
    public static final int EQUIPMENT_WIDGET_INTERFACE = 1644;
    public static final int PRAYER_WIDGET_INTERFACE = 5608;
    public static final int MAGIC_WIDGET_INTERFACE = 1151;
    public static final int FRIEND_WIDGET_INTERFACE = 5065;
    public static final int IGNORE_WIDGET_INTERFACE = 5715;
    public static final int LOGOUT_WIDGET_INTERFACE = 2449;
    public static final int OPTION_WIDGET_INTERFACE = 4445;
    public static final int EMOTE_WIDGET_INTERFACE = 147;
    public static final int MUSIC_WIDGET_INTERFACE = 962;

    private final int interface_;
    private final int icon;

    public GameframeWidgetPacketBuilder(int icon, int interface_) {
        super(PacketBuilderConstants.GAME_FRAME_WIDGET_PACKET_OPCODE, PacketHeader.FIXED);
        this.icon = icon;
        this.interface_ = interface_;
    }

    @Override
    public OutByteBuffer build(Player player) {
        final OutByteBuffer buffer = new OutByteBuffer(Short.BYTES + Byte.BYTES);
        buffer.writeShort(interface_);
        buffer.writeByteA(icon);
        return buffer;
    }
}
