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

import com.florence.model.player.Player;
import com.florence.model.content.skill.SkillSet;
import com.florence.net.packet.builders.ChatboxMessagePacketBuilder;

public enum EquipmentRequirementTable {

    RUNE_WEAPONS(new int[]{1333}, new EquipmentRequirement[]{
        new EquipmentRequirement(SkillSet.ATTACK, 40)
    }),
    RUNE_ARMOUR(new int[]{1079, 1093, 1113, 1147, 1127, 1163, 1185, 1201},
            new EquipmentRequirement[]{
                new EquipmentRequirement(SkillSet.DEFENCE, 40)
            }),
    DRAGON_WEAPONS(new int[]{4587}, new EquipmentRequirement[]{
        new EquipmentRequirement(SkillSet.ATTACK, 60)
    }),
    ABYSALL_WHIP(new int[]{4151}, new EquipmentRequirement[]{
        new EquipmentRequirement(SkillSet.ATTACK, 70)
    }),
    GRANITE_MAUL(new int[]{4153}, new EquipmentRequirement[]{
        new EquipmentRequirement(SkillSet.ATTACK, 50),
        new EquipmentRequirement(SkillSet.STRENGTH, 50)
    });

    private int[] indexes;
    private EquipmentRequirement[] requirements;

    EquipmentRequirementTable(int[] indexes, EquipmentRequirement[] requirements) {
        this.indexes = indexes;
        this.requirements = requirements;
    }

    public static boolean check(Player player, int equipment) {
        for (EquipmentRequirementTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (entry.getIndexes()[index] == equipment) {
                    for (EquipmentRequirement requirement : entry.getRequirements()) {
                        if (requirement.getLevel()
                                > player.getSkillSet().getSkills()[requirement.getSkill()].getLevel()) {
                            String determiner = requirement.getSkill() == SkillSet.ATTACK
                                    || requirement.getSkill() == SkillSet.AGILITY ? "an" : "a";
                            player.encode(new ChatboxMessagePacketBuilder(
                                    "You need " + determiner + " " + SkillSet.SKILL_NAMES[requirement.getSkill()][0].toString().toLowerCase()
                                    + " level of " + requirement.getLevel() + " to equip this item."));
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public int[] getIndexes() {
        return indexes;
    }

    public void setIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    public EquipmentRequirement[] getRequirements() {
        return requirements;
    }

    public void setRequirements(EquipmentRequirement[] requirements) {
        this.requirements = requirements;
    }
}
