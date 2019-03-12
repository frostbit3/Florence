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
package com.florence;

import com.florence.model.World;
import com.florence.net.ChannelDemultiplexer;
import com.florence.net.ChannelDemultiplexerBootstrap;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Florence {

    public static final InetSocketAddress GAME_SERVER_ADDRESS = new InetSocketAddress(43594);
    public static final InetSocketAddress FTP_SERVER_ADDRESS = new InetSocketAddress(43595);

    public static void main(String[] arguments) {
        try {
            World.singleton().initialize();

            /**
             * Initializes the server responsible for transmitting game logic.
             */
            ChannelDemultiplexerBootstrap bootstrap = new ChannelDemultiplexerBootstrap(
                    Executors.newSingleThreadScheduledExecutor(), new ChannelDemultiplexer());
            bootstrap.initialize(GAME_SERVER_ADDRESS);
        } catch (Exception exception) {
            exception.printStackTrace(System.out);
        }
        System.out.println("Florence has successfully been initialized.");
    }
}
