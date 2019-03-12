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
package com.florence.model.item;

import com.florence.model.player.Player;
import com.florence.model.player.PlayerAnimations;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.content.combat.CombatConstants;
import com.florence.model.content.combat.WeaponAnimationTable;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import com.florence.net.packet.builders.GameframeWidgetPacketBuilder;
import com.florence.net.packet.builders.InterfaceTextPacketBuilder;
import com.florence.net.packet.builders.ItemInterfacePacketBuilder;
import com.florence.net.packet.builders.ItemOnInterfacePacketBuilder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EquipmentContainer extends ItemContainer {

    /**
     * The equipment interface.
     */
    public static final int EQUIPMENT_INTERFACE = 1688;

    /**
     * The maximum amount of items a user can have equipped.
     */
    public static final int EQUIPMENT_CAPACITY = 14;

    /**
     * The equipment slot for helmets and hats.
     */
    public static final int EQUIPMENT_SLOT_HEAD = 0;

    /**
     * The equipment slot for capes.
     */
    public static final int EQUIPMENT_SLOT_CAPE = 1;

    /**
     * The equipment slot for amulets.
     */
    public static final int EQUIPMENT_SLOT_AMULET = 2;

    /**
     * The equipment slot for weapons.
     */
    public static final int EQUIPMENT_SLOT_WEAPON = 3;

    /**
     * The equipment slot for tops.
     */
    public static final int EQUIPMENT_SLOT_CHEST = 4;

    /**
     * The equipment slot for shields.
     */
    public static final int EQUIPMENT_SLOT_SHIELD = 5;

    /**
     * The equipment slot for legs.
     */
    public static final int EQUIPMENT_SLOT_LEGS = 7;

    /**
     * The equipment slot for gloves.
     */
    public static final int EQUIPMENT_SLOT_HANDS = 9;

    /**
     * The equipment slot for boots.
     */
    public static final int EQUIPMENT_SLOT_FEET = 10;

    /**
     * The equipment slot for rings and bracelets.
     */
    public static final int EQUIPMENT_SLOT_RING = 12;

    /**
     * The equipment slot for arrows.
     */
    public static final int EQUIPMENT_SLOT_ARROWS = 13;

    /**
     * Item indexes which have complete head coverage.
     */
    public static final Set<Integer> FULL_HEAD_GEAR = new HashSet<Integer>(Arrays.asList(
            new Integer[]{
                6109, 1153, 1155, 1157, 1159, 1161, 1163, 1165, 2587, 2595, 2605, 2613, 2619,
                2627, 2657, 2673, 3486, 4745, 1053, 1055, 1057}));
    /**
     * Item indexes which have complete chest coverage.
     */
    public static final Set<Integer> FULL_BODY_GEAR = new HashSet<Integer>(Arrays.asList(
            new Integer[]{
                6107, 3140, 1115, 1117, 1119, 1121, 1123, 1125, 1127, 1035, 2583, 2591, 2599,
                2607, 2615, 2623, 2653, 2669, 2661, 3481, 4720, 4728, 4749, 4712}));

    public EquipmentContainer(Player player) {
        super(player, EQUIPMENT_CAPACITY);
    }

    public void equip(int index, int slot) {
        if (player.getInventory().contains(index) == false)
            return;
        if (player.getInventory().getItems()[slot].getIndex() != index)
            return;
        if (player.getInventory().getItems()[slot] == null)
            return;
        if (ItemDefinitions.getDefinitions()[index].getEquipmentSlot() == -1)
            return;
        int stackable = 0;
        if (ItemDefinitions.getDefinitions()[index].isTwoHanded() && items[EQUIPMENT_SLOT_SHIELD] != null) {
            if (player.getInventory().available() < InventoryContainer.MINIMUM_ITEM_QUANTITY) {
                player.encode(new ChatboxMessagePacketBuilder("You don't have the required inventory space to equip this item."));
                return;
            }
        }
        if (ItemDefinitions.getDefinitions()[index].isStackable()) {
            stackable = player.getInventory().getItems()[slot].getAmount();
            player.getInventory().unslot(slot, player.getInventory().getItems()[slot].getAmount());
        } else {
            player.getInventory().unslot(slot, 1);
        }
        if (items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()] != null && ItemDefinitions.getDefinitions()[index].isStackable()) {
            if (!contains(index))
                player.getInventory().set(new Item(items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()].getIndex(), items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()].getAmount()), slot);
        } else if (items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()] != null) {
            player.getInventory().set(new Item(items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()].getIndex(), items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()].getAmount()), slot);
        }
        if (ItemDefinitions.getDefinitions()[index].isStackable()) {
            if (contains(index)) {
                items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()].setAmount(items[ItemDefinitions.getDefinitions()[index].getEquipmentSlot()].getAmount() + stackable);
            } else {
                set(new Item(index, stackable), ItemDefinitions.getDefinitions()[index].getEquipmentSlot());
            }
        } else {
            if (ItemDefinitions.getDefinitions()[index].isTwoHanded() && items[EQUIPMENT_SLOT_SHIELD] != null) {
                player.getInventory().add(new Item(items[EQUIPMENT_SLOT_SHIELD].getIndex(), 1));
                unslot(EQUIPMENT_SLOT_SHIELD, 1);
            } else if (items[EQUIPMENT_SLOT_WEAPON] != null && ItemDefinitions.getDefinitions()[items[EQUIPMENT_SLOT_WEAPON].getIndex()].isTwoHanded() && ItemDefinitions.getDefinitions()[index].getEquipmentSlot() == EQUIPMENT_SLOT_SHIELD) {
                player.getInventory().add(new Item(items[EQUIPMENT_SLOT_WEAPON].getIndex(), 1));
                unslot(EQUIPMENT_SLOT_WEAPON, 1);
            }
            set(new Item(index, 1), ItemDefinitions.getDefinitions()[index].getEquipmentSlot());
        }
        refresh();
        player.getUpdateFlags().add(UpdateFlag.APPEARANCE);
    }

    @Override
    public void refresh() {

        /**
         * Refreshes the equipment interface.
         */
        player.encode(new ItemInterfacePacketBuilder(EQUIPMENT_INTERFACE, items));

        /**
         * Refreshes this user's movement animations. Varies by weapon. Critical
         * null check.
         * <p>
         * Determines if this user has a weapon equipped. If the user has no
         * weapon equipped then the table can't be searched and the default
         * values are reset.
         */
        if (items[EQUIPMENT_SLOT_WEAPON] == null) {
            player.getAnimations().setStanding(PlayerAnimations.DEFAULT_IDLE);
            player.getAnimations().setWalking(PlayerAnimations.DEFAULT_WALK);
            player.getAnimations().setRunning(PlayerAnimations.DEFAULT_RUN);
        } else {
            player.getAnimations().setStanding(WeaponAnimationTable.getStandAnimationFor(items[EQUIPMENT_SLOT_WEAPON].getIndex()));
            player.getAnimations().setWalking(WeaponAnimationTable.getWalkAnimationFor(items[EQUIPMENT_SLOT_WEAPON].getIndex()));
            player.getAnimations().setRunning(WeaponAnimationTable.getRunAnimationFor(items[EQUIPMENT_SLOT_WEAPON].getIndex()));
        }

        /**
         * Refreshes the combat interface. Varies by weapon. Critical null
         * check.
         * <p>
         * Determines if the user has a weapon equipped. If the user has no
         * weapon equipped then the default combat interface is reset.
         */
        if (items[EQUIPMENT_SLOT_WEAPON] == null) {
            player.encode(new GameframeWidgetPacketBuilder(0, CombatConstants.UNARMED_WEAPON_INTERFACE));
            player.encode(new InterfaceTextPacketBuilder("Unarmed", 5857));
        } else {
            player.encode(new GameframeWidgetPacketBuilder(0, getWeaponInterface()));
            player.encode(new InterfaceTextPacketBuilder(ItemDefinitions.getDefinitions()[items[EQUIPMENT_SLOT_WEAPON].getIndex()].getName(), getWeaponInterface() + 3));
            player.encode(new ItemOnInterfacePacketBuilder(getWeaponInterface() + 1, 200, items[EQUIPMENT_SLOT_WEAPON].getIndex()));
        }
    }

    private int getWeaponInterface() {
        if (items[EQUIPMENT_SLOT_WEAPON] == null)
            return CombatConstants.UNARMED_WEAPON_INTERFACE;
        for (Object[] WEAPON_INTERFACES : CombatConstants.WEAPON_INTERFACES) {
            if (ItemDefinitions.getDefinitions()[items[EQUIPMENT_SLOT_WEAPON].getIndex()].getName().
                    toLowerCase().contains((String) WEAPON_INTERFACES[0].toString().toLowerCase()))
                return (Integer) WEAPON_INTERFACES[1];
        }
        return CombatConstants.UNARMED_WEAPON_INTERFACE;
    }

    @Override
    public void add(Item item) {
        throw new IllegalStateException("This method has no function in this container.");
    }

    @Override
    public void remove(int index, int amount) {
        throw new IllegalStateException("This method has no function in this container.");
    }
}
