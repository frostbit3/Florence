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
package com.florence.net.packet;

public final class PacketDecoderConstants {

    /**
     * Denotes that the user has pressed a button.
     */
    public static final int ACTION_BUTTON_PACKET_OPCODE = 185;

    /**
     * Denotes that the user has typed a console command.
     */
    public static final int COMMAND_PACKET_OPCODE = 103;

    /**
     * Denotes that the user walked using the map. Has 14 additional (assumed to
     * be anti-cheat) bytes added to the end of it that are ignored.
     */
    public static final int MAP_WALK_PACKET_OPCODE = 248;

    /**
     * Denotes that the user has walked somewhere according to a certain action
     * performed, such as clicking an object.
     */
    public static final int WALK_TO_PACKET_OPCODE = 98;

    /**
     * Denotes that the user has walked normally.
     */
    public static final int STANDARD_WALK_PACKET_OPCODE = 164;

    /**
     * Denotes that the user has equipped an item.
     */
    public static final int EQUIP_ITEM_PACKET_OPCODE = 41;

    /**
     * Denotes that the user has dropped an item.
     */
    public static final int DROP_ITEM_PACKET_OPCODE = 87;

    /**
     * Denotes that the user has switched two items.
     */
    public static final int ITEM_TRANSITION_PACKET_OPCODE = 214;

    /**
     * Denotes the user has selected the 'one' option on an item interface.
     */
    public static final int SELECT_ONE_PACKET_OPCODE = 145;

    /**
     * Denotes the user has selected the 'five' option on an item interface.
     */
    public static final int SELECT_FIVE_PACKET_OPCODE = 117;

    /**
     * Denotes the user has selected the 'ten' option on an item interface.
     */
    public static final int SELECT_TEN_PACKET_OPCODE = 43;

    /**
     * Denotes the user has selected the 'all' option on an item interface.
     */
    public static final int SELECT_ALL_PACKET_OPCODE = 129;

    /**
     * Denotes that the user has closed an interface.
     */
    public static final int CLOSE_INTERFACE_PACKET_OPCODE = 130;

    /**
     * Denotes that the user has clicked on an item.
     */
    public static final int CLICK_ITEM_PACKET_OPCODE = 122;

    /**
     * Denotes that the user has attempted to attack a non-player character.
     */
    public static final int ATTACK_MOB_PACKET_OPCODE = 72;

    /**
     * Denotes that the user has clicked the first option on a non-player
     * character.
     */
    public static final int FIRST_CLICK_MOB_PACKET_OPCODE = 155;

    /**
     * Denotes that the user has typed a message.
     */
    public static final int CHAT_PACKET_OPCODE = 4;
}
