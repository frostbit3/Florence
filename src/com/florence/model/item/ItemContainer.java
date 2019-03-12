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
import java.util.Arrays;

public abstract class ItemContainer {

    protected final Player player;
    protected Item[] items;

    /**
     * Refreshes the interface.
     */
    public abstract void refresh();

    /**
     * Adds a new item.
     * @param item The item to add.
     */
    public abstract void add(Item item);

    /**
     * Removes an existing item quantity.
     * @param index  The index of the existing item.
     * @param amount The quantity to remove.
     */
    public abstract void remove(int index, int amount);

    public ItemContainer(Player player, int capacity) {
        this.player = player;
        this.items = new Item[capacity];
    }

    /**
     * Returns a count of all available slots. An available slot is denoted by a
     * null value.
     * @return The returned count.
     */
    public int available() {
        return (int) Arrays.asList(items).stream().filter(amount -> amount == null).count();
    }

    /**
     * Denotes if a specified item is contained.
     * @param index The index of the item.
     * @return The result.
     */
    public boolean contains(int index) {
        return Arrays.asList(items).stream().anyMatch(item -> (item != null && item.getIndex() == index));
    }

    /**
     * Denotes if a specified item can be held by this container.
     * @param index The index of the item.
     * @return The result.
     */
    public boolean holdable(int index) {
        return available() > 0 || contains(index) && ItemDefinitions.getDefinitions()[index].isStackable();
    }

    /**
     * Assigns an item to a specified slot.
     * @param item The item.
     * @param slot The slot.
     */
    public void set(Item item, int slot) {
        items[slot] = item;
        refresh();
    }

    /**
     * Switches the slots of two items.
     * @param first  The first slot to switch.
     * @param second The second slot to switch.
     */
    public void swap(int first, int second) {
        Item temp = items[first];
        set(items[second], first);
        set(temp, second);
    }

    /**
     * Returns the total count of all items with a specific index.
     * @param index The index to search for.
     * @return The result.
     */
    public final int total(int index) {
        int count = 0;
        for (Item item : items) {
            if (item != null && item.getIndex() == index) {
                /*
                 * If an item can be stacked then there is no need to search
                 * additional slots. The entirety of that item's quantity will
                 * be present in the first slot.
                 */
                if (ItemDefinitions.getDefinitions()[item.getIndex()].isStackable()) {
                    return item.getAmount();
                } else {
                    count += item.getAmount();
                }
            }
        }
        return count;
    }

    /**
     * Removes an item amount from a specified slot.
     * @param slot   The slot.
     * @param amount The amount.
     */
    public void unslot(int slot, int amount) {
        if (items[slot] != null) {
            /*
             * If the item being removed can be stacked then the amount to
             * remove is subtracted from that slot's current amount.
             */
            if (ItemDefinitions.getDefinitions()[items[slot].getIndex()].isStackable()) {
                if (items[slot].getAmount() > amount) {
                    items[slot].setAmount(items[slot].getAmount() - amount);
                } else {
                    items[slot] = null;
                }
            } else {
                items[slot] = null;
            }
        }
        refresh();
    }

    /**
     * Fills in any empty spaces by shifting the positions back by one until all
     * of the empty spaces have been eliminated.
     */
    public void shift() {
        Item[] original = items;

        /*
         * The reorganized item collection.
         */
        items = new Item[items.length];

        for (int slot = 0; slot < items.length; slot++) {
            if (original[slot] != null)
                items[slot] = original[slot];
        }
    }

    public int capacity() {
        return items.length;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }
}
