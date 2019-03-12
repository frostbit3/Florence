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

public enum WeaponTimeTable {

    SCIMITAR(new int[]{1321, 1323, 1325, 1327, 1329, 1331, 1333, 4587}, 3),
    BATTLE_AXE(new int[]{1363, 1365, 1367, 1369, 1371, 1373, 1375, 1377}, 6),
    TWO_HANDED_SWORD(new int[]{1307, 1309, 1311, 1313, 1315, 1317, 1319}, 7),
    DAGGER(new int[]{1203, 1205, 1207, 1209, 1211, 1213, 1215}, 4),
    MACE(new int[]{1420, 1422, 1424, 1426, 1428, 1430, 1432, 1434}, 5),
    SHORTBOW(new int[]{841, 843, 849, 853, 857, 861}, 4),
    ABYSALL_WHIP(new int[]{4151}, 4),
    GRANITE_MAUL(new int[]{4153}, 7);

    private int[] indexes;
    private int speed;

    WeaponTimeTable(int[] indexes, int speed) {
        this.indexes = indexes;
        this.speed = speed;
    }

    public static int getWeaponTime(int weapon) {
        for (WeaponTimeTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (weapon == entry.getIndexes()[index]) {
                    return entry.getSpeed();
                }
            }
        }
        return CombatConstants.DEFAULT_COMBAT_ATTACK_SPEED;
    }

    public int[] getIndexes() {
        return indexes;
    }

    public void setIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
