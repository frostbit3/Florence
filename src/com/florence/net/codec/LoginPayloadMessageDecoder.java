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
import com.florence.util.ByteBufferUtil;
import com.aranea.cryptography.ISAACCipher;
import com.florence.model.player.LoginRequest;
import com.florence.model.World;
import com.florence.net.OutByteBuffer;

public class LoginPayloadMessageDecoder implements MessageDecoder {

    private final int length;

    /**
     * The release number of the client.
     */
    public static final short RELEASE_NUMBER = 317;

    /**
     * The RSA encryption test value.
     */
    public static final int RSA_OPCODE = 0xA;

    /**
     * The amount of archives in the cache.
     */
    public static final int CACHE_ARCHIVES = 9;

    /**
     * Waits two seconds then tries again.
     */
    public static final int WAIT_THEN_TRY_AGAIN_RESPONSE_OPCODE = 1;

    /**
     * A successful login was made.
     */
    public static final int SUCCESSFUL_RESPONSE_OPCODE = 2;

    /**
     * The username or password is incorrect.
     */
    public static final int INVALID_CREDENTIALS_RESPONSE_OPCODE = 3;

    /**
     * This account has been disabled.
     */
    public static final int ACCOUNT_DISABLED_RESPONSE_OPCODE = 4;

    /**
     * This account is already online.
     */
    public static final int ACCOUNT_ACTIVE_RESPONSE_OPCODE = 5;

    /**
     * This login attempt has been rejected.
     */
    public static final int LOGIN_REJECTED_RESPONSE_OPCODE = 11;

    public LoginPayloadMessageDecoder(int length) {
        this.length = length;
    }

    @Override
    public boolean decode(Client client) {
        if (client.getBuffer().remaining() < length)
            return false;
        if ((client.getBuffer().get() & 0xFF) != OutByteBuffer.BYTE_CAPACITY)
            return false;

        /**
         * Reads the release number of the client.
         */
        int release = client.getBuffer().getShort();
        if (release != RELEASE_NUMBER) {
            client.encode(new LoginPayloadResponse(LOGIN_REJECTED_RESPONSE_OPCODE, 0, false));
            return false;
        }

        /**
         * Reads the client's memory mode.
         */
        int memory = client.getBuffer().get() & 0xFF;
        if (memory != 0 && memory != 1) {
            client.encode(new LoginPayloadResponse(LOGIN_REJECTED_RESPONSE_OPCODE, 0, false));
            return false;
        }

        /**
         * Reads the archive redundancy checks.
         */
        int crcs[] = new int[CACHE_ARCHIVES];
        for (int index = 0; index < crcs.length; index++) {
            crcs[index] = client.getBuffer().getInt();
        }

        /**
         * Compares the expected versus the received lengths. Offset appended.
         */
        int length_ = client.getBuffer().get() & 0xFF;
        if (length - (36 + 1 + 1 + 1 + 2) != length_) {
            client.encode(new LoginPayloadResponse(LOGIN_REJECTED_RESPONSE_OPCODE, 0, false));
            return false;
        }

        /**
         * Reads a test value for RSA encryption.
         */
        int rsa = client.getBuffer().get() & 0xFF;
        if (rsa != RSA_OPCODE) {
            client.encode(new LoginPayloadResponse(LOGIN_REJECTED_RESPONSE_OPCODE, 0, false));
            return false;
        }

        /**
         * The client's authorization key.
         */
        long clientAuth = client.getBuffer().getLong();

        /**
         * The server's authorization key.
         */
        long serverAuth = client.getBuffer().getLong();

        final int[] seeds = new int[4];
        seeds[0] = (int) (clientAuth >> 32);
        seeds[1] = (int) clientAuth;
        seeds[2] = (int) (serverAuth >> 32);
        seeds[3] = (int) serverAuth;

        /**
         * The decryption algorithm.
         */
        client.setDecryption(new ISAACCipher(seeds));

        for (int index = 0; index < seeds.length; index++) {
            seeds[index] += 50;
        }

        /**
         * The encryption algorithm.
         */
        client.setEncryption(new ISAACCipher(seeds));

        /**
         * The unique identification index for this client. Not readily used in
         * emulation.
         */
        client.getBuffer().getInt();

        /**
         * The player's username.
         */
        String username = ByteBufferUtil.readString(client.getBuffer());

        /**
         * The player's password.
         */
        String password = ByteBufferUtil.readString(client.getBuffer());

        /**
         * The result.
         */
        int result = SUCCESSFUL_RESPONSE_OPCODE;

        /**
         * This account is already logged in.
         */
        result = World.singleton().contains(username) ? ACCOUNT_ACTIVE_RESPONSE_OPCODE : result;

        /**
         * Writes the result.
         */
        client.encode(new LoginPayloadResponse(result, client.getPlayer().getRights(), false));

        /**
         * A successful login was made.
         */
        if (result == SUCCESSFUL_RESPONSE_OPCODE) {

            /**
             * Sets the details.
             */
            client.getPlayer().setUsername(username);
            client.getPlayer().setPassword(password);

            /**
             * Queues this login. Will be processed on the next iteration of the
             * world's thread.
             */
            World.singleton().getLogins().add(new LoginRequest(client.getPlayer()));

            /**
             * Readies the next set of encoders and decoders for game packets.
             */
            client.setCodecs(new GameMessageEncoder(), new GameMessageDecoder());
        }

        return result == SUCCESSFUL_RESPONSE_OPCODE;
    }
}
