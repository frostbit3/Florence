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

import com.florence.util.StringUtil;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ItemDefinitions {

    /**
     * The path to the data file.
     */
    public static final String DATA_FILE_PATH = "./data/items/items.dat";

    /**
     * The item definitions.
     */
    private static final ItemDefinitions[] definitions = new ItemDefinitions[7956];

    /**
     * The item's index.
     */
    private int index;

    /**
     * The index of the item while in the form of a note.
     */
    private int notedIndex;

    /**
     * The price this item is sold for.
     */
    private int storePrice;

    /**
     * The amount of coins received when a low level alchemy spell is cast on
     * the item.
     */
    private int lowAlchValue;

    /**
     * The amount of coins received when a high level alchemy spell is cast on
     * the item.
     */
    private int highAlchValue;

    /**
     * The slot where this item is equipped.
     */
    private int equipmentSlot;

    /**
     * The item's name.
     */
    private String name;

    /**
     * The item's description.
     */
    private String description;

    /**
     * Denotes if this item can be stacked.
     */
    private boolean stackable;

    /**
     * Denotes if this item can be noted.
     */
    private boolean notable;

    /**
     * Denotes if this item requires two hands to equip.
     */
    private boolean twoHanded;

    /**
     * This item's equipment bonuses.
     */
    private int[] bonuses = new int[12];

    /**
     * Parses the definitions from an external data file.
     * @throws java.lang.Exception The exception thrown if an error occurs while
     *                             parsing the file.
     */
    public static void load() throws Exception {
        final DataInputStream stream = new DataInputStream(
                new FileInputStream(new File(DATA_FILE_PATH)));
        for (int pointer = 0; stream.available() > 0; pointer++) {
            final ItemDefinitions definition = new ItemDefinitions();

            definition.setIndex(stream.readShort());
            definition.setName(StringUtil.format(StringUtil.read(stream)));
            definition.setDescription(StringUtil.read(stream));
            definition.setStackable(stream.readByte() == 1);
            definition.setNotedIndex(stream.readShort());
            definition.setNotable(definition.getNotedIndex() != -1 ? stream.readByte() == 1 : false);
            definition.setStorePrice(stream.readInt());
            definition.setLowAlchValue(stream.readInt());
            definition.setHighAlchValue(stream.readInt());
            definition.setEquipmentSlot(stream.readByte());
            if (definition.getEquipmentSlot() != -1) {
                for (int i = 0; i < definition.getBonuses().length; i++) {
                    definition.getBonuses()[i] = stream.readByte();
                }
            }
            if (definition.getName().contains("2h") || definition.getName().contains("bow") || definition.getName().contains("maul")) {
                definition.setTwoHanded(true);
            }
            if (definition.getEquipmentSlot() == -1)
                definition.setEquipmentSlot(EquipmentContainer.EQUIPMENT_SLOT_CAPE);
            switch (definition.getIndex()) {

                case 1033:
                    definition.setEquipmentSlot(EquipmentContainer.EQUIPMENT_SLOT_LEGS);
                    break;
            }
            definitions[pointer] = definition;
        }
        System.out.println("Loaded " + definitions.length + " item definitions.");
    }

    /**
     * Returns the parsed item definitions.
     * @return The returned definitions.
     */
    public static ItemDefinitions[] getDefinitions() {
        return definitions;
    }

    /**
     * Returns the item's index.
     * @return The returned index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Mutates the item's index.
     * @param index The mutation.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Returns the item's index while noted.
     * @return The returned index.
     */
    public int getNotedIndex() {
        return notedIndex;
    }

    /**
     * Mutates the item's index while noted.
     * @param notedIndex The mutation.
     */
    public void setNotedIndex(int notedIndex) {
        this.notedIndex = notedIndex;
    }

    /**
     * Returns the item's price.
     * @return The returned price.
     */
    public int getStorePrice() {
        return storePrice;
    }

    /**
     * Mutates the item's price.
     * @param storePrice The mutation.
     */
    public void setStorePrice(int storePrice) {
        this.storePrice = storePrice;
    }

    /**
     * Returns the item's value when a low alchemy spell is cast upon it.
     * @return The returned value.
     */
    public int getLowAlchValue() {
        return lowAlchValue;
    }

    /**
     * Mutates the item's value when a low alchemy spell is cast upon it.
     * @param lowAlchValue The mutation.
     */
    public void setLowAlchValue(int lowAlchValue) {
        this.lowAlchValue = lowAlchValue;
    }

    /**
     * Returns the item's value when a high alchemy spell is cast upon it.
     * @return The returned value.
     */
    public int getHighAlchValue() {
        return highAlchValue;
    }

    /**
     * Mutates the item's value when a high alchemy spell is cast upon it.
     * @param highAlchValue The mutation.
     */
    public void setHighAlchValue(int highAlchValue) {
        this.highAlchValue = highAlchValue;
    }

    /**
     * Returns the item's equipment slot.
     * @return The returned slot.
     */
    public int getEquipmentSlot() {
        return equipmentSlot;
    }

    /**
     * Mutates the item's equipment slot.
     * @param equipmentSlot The mutation.
     */
    public void setEquipmentSlot(int equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    /**
     * Returns the item's name.
     * @return The returned name.
     */
    public String getName() {
        return name;
    }

    /**
     * Mutates the item's name.
     * @param name The mutation.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the item's description.
     * @return The returned description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Mutates the item's description.
     * @param description The mutation.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Denotes if the item can be stacked.
     * @return The result.
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * Mutates if the item can be stacked.
     * @param stackable The mutation.
     */
    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    /**
     * Denotes if the item can be noted.
     * @return The result.
     */
    public boolean isNotable() {
        return notable;
    }

    /**
     * Mutates if the item can be noted.
     * @param notable The mutation.
     */
    public void setNotable(boolean notable) {
        this.notable = notable;
    }

    /**
     * Denotes if the item is held in both hands.
     * @return The result.
     */
    public boolean isTwoHanded() {
        return twoHanded;
    }

    /**
     * Mutates if the item is held in both hands.
     * @param twoHanded The mutation.
     */
    public void setTwoHanded(boolean twoHanded) {
        this.twoHanded = twoHanded;
    }

    /**
     * Returns the item's combat bonuses.
     * @return The returned bonuses.
     */
    public int[] getBonuses() {
        return bonuses;
    }

    /**
     * Mutates the item's combat bonuses.
     * @param bonuses The mutation.
     */
    public void setBonuses(int[] bonuses) {
        this.bonuses = bonuses;
    }
}
