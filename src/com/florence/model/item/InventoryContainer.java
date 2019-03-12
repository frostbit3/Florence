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
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import com.florence.net.packet.builders.ItemInterfacePacketBuilder;

public class InventoryContainer extends ItemContainer {

    /**
     * The maximum amount of items that a user's inventory can hold.
     */
    public static final int INVENTORY_CAPACITY = 28;

    /**
     * The inventory interface.
     */
    public static final int INVENTORY_INTERFACE = 3214;

    /**
     * The minimum quantity an item can have.
     */
    public static final int MINIMUM_ITEM_QUANTITY = 1;

    public InventoryContainer(Player player) {
        super(player, INVENTORY_CAPACITY);
    }

    @Override
    public void refresh() {
        player.encode(new ItemInterfacePacketBuilder(INVENTORY_INTERFACE, items));
    }

    @Override
    public void add(Item item) {
        if (item.getIndex() > ItemDefinitions.getDefinitions().length)
            return;
        if ((available() == 0)
                /**
                 * Doesn't contain this item in a stack.
                 */
                && !(contains(item.getIndex())
                && ItemDefinitions.getDefinitions()[item.getIndex()].isStackable())
                /**
                 * Doesn't contain this item as a note.
                 */
                && !(contains(item.getIndex() + 1)
                && ItemDefinitions.getDefinitions()[item.getIndex()].isNotable())) {
            player.encode(new ChatboxMessagePacketBuilder("You don't have the required inventory space to hold this item."));
            return;
        }
        for (int slot = 0; slot < capacity(); slot++) {
            if (items[slot] == null)
                continue;
            /**
             * This item is stackable and already contained.
             */
            if (items[slot].getIndex() == item.getIndex()
                    && ItemDefinitions.getDefinitions()[item.getIndex()].isStackable()) {
                items[slot].setAmount(items[slot].getAmount() + item.getAmount());
                refresh();
                return;
            }
        }
        /**
         * This item is not stackable and the quantity is more than one.
         */
        if (!ItemDefinitions.getDefinitions()[item.getIndex()].isStackable()
                && item.getAmount() > MINIMUM_ITEM_QUANTITY) {
            final int amount = item.getAmount();
            for (int count = 0; count < amount; count++) {
                for (int slot = 0; slot < capacity(); slot++) {
                    if (items[slot] == null) {
                        items[slot] = item;
                        /**
                         * Since this item isn't stackable, add each item
                         * individually with a quantity of one.
                         */
                        item.setAmount(MINIMUM_ITEM_QUANTITY);
                        break;
                    }
                }
            }
            refresh();
            return;
        }
        /**
         * Finds the first available slot.
         */
        for (int slot = 0; slot < capacity(); slot++) {
            if (items[slot] == null) {
                items[slot] = item;
                /**
                 * An available slot was found.
                 */
                break;
            }
        }
        refresh();
    }

    @Override
    public void remove(int index, int amount) {
        int count = 0;
        for (int slot = 0; slot < capacity(); slot++) {
            if (items[slot] == null)
                continue;
            if (items[slot].getIndex() == index) {
                if (count == amount)
                    break;
                if (items[slot].getAmount() > amount && ItemDefinitions.getDefinitions()[index].isStackable()) {
                    items[slot].setAmount(items[slot].getAmount() - amount);
                    break;
                }
                items[slot] = null;
                count++;
            }
        }
        refresh();
    }
}
