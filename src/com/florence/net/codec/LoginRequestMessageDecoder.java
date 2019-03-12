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

public class LoginRequestMessageDecoder implements MessageDecoder {

    /**
     * Denotes a new connection.
     */
    public static final int FRESH_CONNECTION = 0x10;

    /**
     * Denotes a re-establishment of an existing connection.
     */
    public static final int EXISTING_CONNECTION = 0x12;

    @Override
    public boolean decode(Client client) {
        int connectionType = client.getBuffer().get() & 0xFF;
        if (connectionType == FRESH_CONNECTION || connectionType == EXISTING_CONNECTION) {
            int length = client.getBuffer().get() & 0xFF;

            client.setCodecs(new LoginPayloadMessageEncoder(),
                    new LoginPayloadMessageDecoder(length));
        }
        return connectionType == FRESH_CONNECTION || connectionType == EXISTING_CONNECTION;
    }
}
