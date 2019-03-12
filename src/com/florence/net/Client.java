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

import com.aranea.cryptography.ISAACCipher;
import com.florence.model.player.LogoutRequest;
import com.florence.model.player.Player;
import com.florence.model.World;
import com.florence.net.codec.MessageDecoder;
import com.florence.net.codec.MessageEncoder;
import com.florence.net.codec.ServiceRequestMessageDecoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Client {

    /**
     * The maximum amount of bytes that can be read in a single iteration.
     */
    public static final int IN_BUFFER_CAPACITY = 512;

    private SelectionKey token;
    private SocketChannel socket;
    private final ByteBuffer buffer = ByteBuffer.allocate(IN_BUFFER_CAPACITY);

    private MessageEncoder encoder;
    private MessageDecoder decoder;

    private boolean disconnected;

    private ISAACCipher encryption;
    private ISAACCipher decryption;

    /**
     * The user associated with this connection.
     */
    private final Player player = new Player(this);

    public Client(SelectionKey token, SocketChannel socket) {
        this.token = token;
        this.socket = socket;

        this.decoder = new ServiceRequestMessageDecoder();
    }

    public void encode(Object message) {
        if (encoder == null)
            throw new NullPointerException("A valid encoder must be registered.");
        write(encoder.encode(this, message));
    }

    public void write(ByteBuffer source) {
        source.flip();
        try {
            socket.write(source);
        } catch (IOException exception) {
            disconnect();
        }
    }

    public void read() {
        if (decoder == null)
            throw new NullPointerException("A valid decoder must be registered.");
        if (disconnected)
            return;
        try {
            buffer.clear();
            if (socket.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    if (!decoder.decode(this))
                        disconnect();
                }
            }
        } catch (IOException exception) {
            disconnect();
        }
    }

    public void disconnect() {
        if (disconnected)
            return;
        disconnected = true;

        /**
         * Queues this logout request.
         */
        World.singleton().getLogouts().add(new LogoutRequest(player));
        try {
            /**
             * Closes this channel.
             */
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace(System.out);
        } finally {
            /**
             * Requests that the registration of this key's channel with its
             * selector be canceled.
             */
            token.cancel();
        }
    }

    public void setCodecs(MessageEncoder encoder, MessageDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public Player getPlayer() {
        return player;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public SelectionKey getToken() {
        return token;
    }

    public void setToken(SelectionKey token) {
        this.token = token;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public MessageEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(MessageEncoder encoder) {
        this.encoder = encoder;
    }

    public MessageDecoder getDecoder() {
        return decoder;
    }

    public void setDecoder(MessageDecoder decoder) {
        this.decoder = decoder;
    }

    public ISAACCipher getEncryption() {
        return encryption;
    }

    public void setEncryption(ISAACCipher encryption) {
        this.encryption = encryption;
    }

    public ISAACCipher getDecryption() {
        return decryption;
    }

    public void setDecryption(ISAACCipher decryption) {
        this.decryption = decryption;
    }

    public boolean disconnected() {
        return disconnected;
    }
}
