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
package com.florence.model;

import com.florence.model.mob.Mob;
import com.florence.model.player.Player;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;

public class WalkToActions {

    public static final int NORMAL_SHORT_SLEEVE_MAN_IDENTITY = 1;
    public static final int NORMAL_LONG_SLEEVE_MAN_IDENTITY = 2;

    public static void firstOptionMob(Mob mob, Player player) {
        switch (mob.getIdentity()) {

            case NORMAL_SHORT_SLEEVE_MAN_IDENTITY:
            case NORMAL_LONG_SLEEVE_MAN_IDENTITY:
                player.encode(new ChatboxMessagePacketBuilder("He doesn't seem interested in talking right now."));
                break;
        }
    }
}
