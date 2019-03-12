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

public final class PacketBuilderConstants {

    /**
     * Displays a message in the user's chat console.
     */
    public static final int CHATBOX_MESSAGE_PACKET_OPCODE = 253;

    /**
     * Updates the users within the world.
     */
    public static final int UPDATE_PLAYER_PACKET_OPCODE = 81;

    /**
     * Updates the non-player characters within the world.
     */
    public static final int UPDATE_MOB_PACKET_OPCODE = 65;

    /**
     * Updates this user's region.
     */
    public static final int CONSTRUCT_REGION_PACKET_OPCODE = 73;

    /**
     * Displays a widget on the user's game-frame.
     */
    public static final int GAME_FRAME_WIDGET_PACKET_OPCODE = 71;

    /**
     * Sends a configuration state to the user's client.
     */
    public static final int CLIENT_CONFIGURATION_PACKET_OPCODE = 36;

    /**
     * Logs a user out.
     */
    public static final int LOGOUT_USER_PACKET_OPCODE = 109;

    /**
     * Logs a user in.
     */
    public static final int LOGIN_USER_PACKET_OPCODE = 107;

    /**
     * Dispatches the user's login details.
     */
    public static final int LOGIN_DETAILS_PACKET_OPCODE = 249;

    /**
     * Displays a user's run energy.
     */
    public static final int DISPLAY_RUN_ENERGY_PACKET_OPCODE = 110;

    /**
     * Displays items on an interface.
     */
    public static final int ITEM_INTERFACE_PACKET_OPCODE = 53;

    /**
     * Displays a singular item on an interface.
     */
    public static final int ITEM_ON_INTERFACE_PACKET_OPCODE = 246;

    /**
     * Writes text on an interface.
     */
    public static final int INTERFACE_TEXT_PACKET_OPCODE = 126;

    /**
     * Displays a user's skill levels on an interface.
     */
    public static final int SKILL_INTERFACE_PACKET_OPCODE = 134;
}
