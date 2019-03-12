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
package com.florence.model.content.combat;

public class CombatConstants {

    public static final int[] BOWS = {837, 839, 841, 843, 845, 847, 849, 851, 853, 855, 857, 859, 861, 4734, 4212, 4214};
    public static final int[] THROWING_KNIVES = {863, 864, 865, 866, 867, 868, 869, 870, 871, 872, 873, 874, 875, 876};
    public static final int[] DARTS = {806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 817};

    public static final Object[][] WEAPON_INTERFACES = {{"WHIP", 12290}, {"BOW", 1764}, {"STAFF", 328}, {"WAND", 328}, {"DART", 4446}, {"KNIFE", 4446},
    {"JAVELIN", 4446}, {"DAGGER", 2276}, {"SWORD", 2276}, {"SCIMITAR", 2276}, {"PICKAXE", 5570}, {"AXE", 1698}, {"BATTLEAXE", 1698}, {"HALBERD", 8460}, {"SPEAR", 4679}, {"MACE", 3796}, {"WARHAMMER", 425}, {"MAUL", 425}};

    public static final int DEFAULT_COMBAT_ATTACK_SPEED = 3;
    public static final int COMBAT_SPELLS_STANDARD_SPEED = 3;
    public static final int COMBAT_SPELLS_LONGRANGE_SPEED = 5;

    public static final double RAPID_RANGE_STYLE_TIME_MODIFIER = 0.6;
    public static final int ACCURATE_RANGE_STYLE_RANGE_LEVEL_MODIFIER = 3;
    public static final int LONGRANGE_RANGE_STYLE_DISTANCE_MODIFIER = 2;

    public static final int AGGRESSIVE_ATTACK_STYLE_STRENGTH_LEVEL_MODIFIER = 3;
    public static final int ACCURATE_ATTACK_STYLE_ATTACK_LEVEL_MODIFIER = 3;
    public static final int DEFENSIVE_ATTACK_STYLE_DEFENSE_LEVEL_MODIFIER = 3;
    public static final int CONTROLLED_ATTACK_STYLE_LEVEL_MODIFIERS = 1;

    public static final int UNARMED_WEAPON_INTERFACE = 5855;
}
