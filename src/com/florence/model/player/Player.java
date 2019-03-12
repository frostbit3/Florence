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
package com.florence.model.player;

import com.florence.model.Entity;
import com.florence.model.GameConstants;
import com.florence.model.mob.Mob;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.World;
import com.florence.model.content.TeleportSpell;
import com.florence.model.content.skill.SkillSet;
import com.florence.model.item.EquipmentContainer;
import com.florence.model.item.InventoryContainer;
import com.florence.net.Client;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketBuilder;
import com.florence.net.packet.builders.ClientConfigurationPacketBuilder;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import com.florence.net.packet.builders.GameframeWidgetPacketBuilder;
import com.florence.net.packet.builders.LoginDetailsPacketBuilder;
import com.florence.net.packet.builders.LoginPacketBuilder;
import com.florence.net.packet.builders.MobUpdatePacketBuilder;
import com.florence.net.packet.builders.PlayerUpdatePacketBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Player extends Entity {

    private final Client client;

    private String username;
    private String password;
    private boolean connected;
    private final ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();
    private final PlayerAnimations animations = new PlayerAnimations();

    /**
     * This user's access level.
     */
    private int rights = 0;

    /**
     * The chat text.
     */
    private byte chat[] = new byte[256];

    /**
     * The chat effects.
     */
    private int chatEffects = 0;

    /**
     * The chat color
     */
    private int chatColor = 0;

    /**
     * This user's skills.
     */
    private final SkillSet skills = new SkillSet(this);

    /**
     * This user's clothing, hair-style, and gender.
     */
    private final Appearance appearance = new Appearance(this);

    /**
     * Other players that are currently within view.
     */
    private final List<Player> localPlayers = new LinkedList<>();

    /**
     * Non-player characters that are currently within view.
     */
    private final List<Mob> localMobs = new LinkedList<>();

    /**
     * This user's inventory.
     */
    private final InventoryContainer inventory = new InventoryContainer(this);

    /**
     * This user's equipment.
     */
    private final EquipmentContainer equipment = new EquipmentContainer(this);

    /**
     * Denotes the status of this user's current region.
     */
    private RegionState r_state = RegionState.REBUILDING_REGION;

    public enum RegionState {

        /**
         * The user is traversing their current region. No action is required.
         */
        TRAVERSING_REGION,
        /**
         * This user has left their current region and needs their new region to
         * be built.
         */
        REBUILDING_REGION;
    }

    /**
     * Denotes the last time this user performed a teleportation spell via a
     * pressed button.
     */
    private long lastTeleportButton;

    /**
     * The frequency in milliseconds that a teleportation spell can be cast via
     * a pressed button.
     */
    public static final int TELEPORT_BUTTON_TIME_DELAY = 4000;

    public Player(Client client) {
        this.client = client;
    }

    @Override
    public void add() {
        position.set(GameConstants.DEFAULT_POSITION);
        region.set(GameConstants.DEFAULT_POSITION);

        inventory.refresh();
        equipment.refresh();
        skills.refresh();
        flags.add(UpdateFlag.APPEARANCE);

        encode(new ChatboxMessagePacketBuilder("Welcome to Florence."));
        encode(new ClientConfigurationPacketBuilder(
                ClientConfigurationPacketBuilder.RUN_BUTTON_CONFIGURATION_INDEX, 0));
        encode(new GameframeWidgetPacketBuilder(1,
                GameframeWidgetPacketBuilder.SKILL_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(2,
                GameframeWidgetPacketBuilder.QUEST_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(3,
                GameframeWidgetPacketBuilder.INVENTORY_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(4,
                GameframeWidgetPacketBuilder.EQUIPMENT_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(5,
                GameframeWidgetPacketBuilder.PRAYER_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(6,
                GameframeWidgetPacketBuilder.MAGIC_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(8,
                GameframeWidgetPacketBuilder.FRIEND_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(9,
                GameframeWidgetPacketBuilder.IGNORE_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(10,
                GameframeWidgetPacketBuilder.LOGOUT_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(11,
                GameframeWidgetPacketBuilder.OPTION_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(12,
                GameframeWidgetPacketBuilder.EMOTE_WIDGET_INTERFACE));
        encode(new GameframeWidgetPacketBuilder(13,
                GameframeWidgetPacketBuilder.MUSIC_WIDGET_INTERFACE));
        encode(new LoginPacketBuilder());
        encode(new LoginDetailsPacketBuilder(index, true));

        /**
         * Adds this user to the world.
         */
        World.singleton().getPlayers().add(this);
        System.out.println(username + " has been added to the world. There are now "
                + World.singleton().getPlayers().size() + " users online.");
        connected = true;
    }

    @Override
    public void remove() {
        if (!client.disconnected() || !connected) {
            System.err.println(
                    "To prevent synchronization concerns, a user's connection must be closed "
                    + "before they can be removed. If you're attempting to remove a user, "
                    + "please use the method com.florence.model.Player#disconnect to do so safely.");
            return;
        }

        /**
         * Removes this user from the world.
         */
        World.singleton().getPlayers().remove(index);

        /**
         * Cancel any tasks that this user may have submitted.
         */
        World.singleton().getTasks().cancel(this);

        System.out.println("Removed " + username + " there are now "
                + World.singleton().getPlayers().size() + " users online.");
        connected = false;
    }

    public void encode(PacketBuilder message) {
        client.encode(message);
    }

    public void disconnect() {
        client.disconnect();
    }

    public void teleport(TeleportSpell spell, boolean button) {
        /**
         * Determines if this spell was triggered via a button press. If so, a
         * delay must be implemented to prevent mass triggering.
         */
        if (button) {
            if (System.currentTimeMillis() - lastTeleportButton < TELEPORT_BUTTON_TIME_DELAY) {
                encode(new ChatboxMessagePacketBuilder("Please wait a few moments before attempting to "
                        + "cast a spell again."));
                return;
            } else {
                lastTeleportButton = System.currentTimeMillis();
            }
        }
        teleport(spell);
    }

    public int getWeapon() {
        return equipment.getItems()[EquipmentContainer.EQUIPMENT_SLOT_WEAPON] == null ? -1
                : equipment.getItems()[EquipmentContainer.EQUIPMENT_SLOT_WEAPON].getIndex();
    }

    @Override
    public void update() {
        encode(new PlayerUpdatePacketBuilder());
        encode(new MobUpdatePacketBuilder());
    }

    @Override
    public void reset() {
        flags.reset();
        r_state = RegionState.TRAVERSING_REGION;
    }

    @Override
    public void process() {
        steps.process();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Client getClient() {
        return client;
    }

    public ConcurrentLinkedQueue<Packet> getPackets() {
        return packets;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public List<Player> getLocalPlayers() {
        return localPlayers;
    }

    public List<Mob> getLocalMobs() {
        return localMobs;
    }

    public RegionState getRegionState() {
        return r_state;
    }

    public void setRegionState(RegionState r_state) {
        this.r_state = r_state;
    }

    public InventoryContainer getInventory() {
        return inventory;
    }

    public EquipmentContainer getEquipment() {
        return equipment;
    }

    public PlayerAnimations getAnimations() {
        return animations;
    }

    public SkillSet getSkillSet() {
        return skills;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public byte[] getChat() {
        return chat;
    }

    public void setChat(byte[] chat) {
        this.chat = chat;
    }

    public int getChatEffects() {
        return chatEffects;
    }

    public void setChatEffects(int chatEffects) {
        this.chatEffects = chatEffects;
    }

    public int getChatColor() {
        return chatColor;
    }

    public void setChatColor(int chatColor) {
        this.chatColor = chatColor;
    }

    public int getRights() {
        return rights;
    }

    public void setRights(int rights) {
        this.rights = rights;
    }
}
