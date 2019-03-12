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
package com.florence.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ChannelDemultiplexer implements Runnable {

    /**
     * Denotes the blocking mode.
     */
    public static final boolean CHANNEL_BLOCKS = false;

    private Selector selector;
    private ServerSocketChannel server;

    public ChannelDemultiplexer() throws IOException {
        this(Selector.open(), ServerSocketChannel.open());
    }

    public ChannelDemultiplexer(Selector selector, ServerSocketChannel server) {
        this.selector = selector;
        this.server = server;
    }

    public void initialize(InetSocketAddress address) throws IOException {
        server.configureBlocking(CHANNEL_BLOCKS);
        server.register(selector, SelectionKey.OP_ACCEPT);
        server.bind(address);
    }

    @Override
    public void run() {
        try {
            selector.selectNow();
        } catch (IOException exception) {
            exception.printStackTrace(System.out);
        }
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            try {
                SelectionKey token = iterator.next();
                if (token.isValid()) {
                    if (token.isAcceptable()) {
                        SocketChannel socket = server.accept();
                        if (socket == null)
                            return;
                        socket.configureBlocking(CHANNEL_BLOCKS);
                        SelectionKey register = socket.register(selector, SelectionKey.OP_READ);

                        register.attach(new Client(register, socket));
                    } else if (token.isReadable()) {
                        Client client = (Client) token.attachment();
                        if (client == null)
                            return;
                        client.read();
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace(System.out);
            }
            iterator.remove();
        }
    }
}
