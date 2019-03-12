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

public class ServiceRequestMessageDecoder implements MessageDecoder {

    /**
     * Denotes a new login request.
     */
    public static final int LOGIN_REQUEST = 0xE;

    /**
     * Denotes a new update request.
     */
    public static final int UPDATE_REQUEST = 0xF;

    @Override
    public boolean decode(Client client) {
        int request = client.getBuffer().get() & 0xFF;
        if (request == LOGIN_REQUEST)
            client.setCodecs(new LoginHandshakeMessageEncoder(),
                    new LoginHandshakeMessageDecoder());
        return request == LOGIN_REQUEST;
    }
}
