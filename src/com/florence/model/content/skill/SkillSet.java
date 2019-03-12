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
package com.florence.model.content.skill;

import com.florence.model.player.Player;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;
import com.florence.net.packet.builders.SkillInterfacePacketBuilder;

public class SkillSet {

    public static final int MINIMUM_SKILL_LEVEL = 1;
    public static final int MAXIMUM_SKILL_LEVEL = 99;
    public static final int AMOUNT_OF_SKILLS = 21;

    public static final int ATTACK = 0;
    public static final int DEFENCE = 1;
    public static final int STRENGTH = 2;
    public static final int HITPOINTS = 3;
    public static final int RANGE = 4;
    public static final int PRAYER = 5;
    public static final int MAGIC = 6;
    
    public static final int COOKING = 7;
    public static final int WOODCUTTING = 8;
    public static final int FLETCHING = 9;
    public static final int FISHING = 10;
    public static final int FIREMAKING = 11;
    public static final int CRAFTING = 12;
    public static final int SMITHING = 13;
    public static final int MINING = 14;
    public static final int HERBLORE = 15;
    public static final int AGILITY = 16;
    public static final int THIEVING = 17;
    public static final int SLAYER = 18;
    public static final int FARMING = 19;
    public static final int RUNECRAFTING = 20;

    public static final Object[][] SKILL_NAMES = 
    {
        {"Attack", ATTACK},             {"Defence", DEFENCE},
        {"Strength", STRENGTH},         {"Hitpoints", HITPOINTS},
        {"Range", RANGE},               {"Prayer", PRAYER},
        {"Magic", MAGIC},               {"Cooking", COOKING},
        {"Woodcutting", WOODCUTTING},   {"Fletching", FLETCHING},
        {"Fishing", FISHING},           {"Firemaking", FIREMAKING},
        {"Crafting", CRAFTING},         {"Smithing", SMITHING},
        {"Mining", MINING},             {"Herblore", HERBLORE},
        {"Agility", AGILITY},           {"Thieving", THIEVING},
        {"Slayer", SLAYER},             {"Farming", FARMING},
        {"Runecrafting", RUNECRAFTING}
    };

    private final Skill[] skills = new Skill[AMOUNT_OF_SKILLS];
    private final Player player;

    public SkillSet(Player player) {
        this.player = player;

        /**
         * Sets the default values.
         */
        for (int skill = 0; skill < AMOUNT_OF_SKILLS; skill++) {
            skills[skill] = new Skill(1, 0);
        }

        /**
         * By default, a user's constitution level is ten.
         */
        skills[HITPOINTS] = new Skill(10, 1184);
    }

    public void addExperience(int skill, double experience) {
        int last = skills[skill].getLevel();
        skills[skill].setExperience(getSkills()[skill].getExperience() + experience);
        skills[skill].setLevel(getLevel(skill));
        int current = skills[skill].getLevel();

        /**
         * The user has increased a level.
         */
        if ((current - last) > 0) {
            player.encode(new ChatboxMessagePacketBuilder(
                    "Congratulations! You have advanced a " + SKILL_NAMES[skill][0] + " level. Your level is now " + current + "."));
            if (skill >= ATTACK && skill <= MAGIC) {

                /**
                 * Update the user's combat level.
                 */
                player.getUpdateFlags().add(UpdateFlag.APPEARANCE);
            }
        }
        refresh(skill);
    }

    public int getLevel(int skill) {
        int experience = 0;
        for (int level = MINIMUM_SKILL_LEVEL; level <= MAXIMUM_SKILL_LEVEL; level++) {
            experience = (int) (experience + Math.floor(level + 300.0D * Math.pow(2.0D, level / 7.0D)));
            if (Math.floor(experience / 4) >= skills[skill].getExperience())
                return level;
        }
        return MAXIMUM_SKILL_LEVEL;
    }

    public double getCombatLevel() {
        final double base = .25 * (skills[DEFENCE].getLevel() + skills[HITPOINTS].getLevel() + Math.floor(skills[PRAYER].getLevel() / 2));
        final double melee = .325 * (skills[ATTACK].getLevel() + skills[STRENGTH].getLevel());
        final double range = .325 * (Math.floor(skills[RANGE].getLevel() / 2) + skills[RANGE].getLevel());
        final double magic = .325 * (Math.floor(skills[MAGIC].getLevel() / 2) + skills[MAGIC].getLevel());

        return Math.floor(base + Math.max(melee, Math.max(magic, range)));
    }

    public void refresh() {
        for (int skill = 0; skill < skills.length; skill++) {
            player.encode(new SkillInterfacePacketBuilder(skill, (int) skills[skill].getExperience(),
                    skills[skill].getLevel()));
        }
    }

    public void refresh(int skill) {
        player.encode(new SkillInterfacePacketBuilder(skill, (int) skills[skill].getExperience(),
                skills[skill].getLevel()));
    }

    public Skill[] getSkills() {
        return skills;
    }

    public Player getPlayer() {
        return player;
    }
}
