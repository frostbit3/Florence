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
package com.florence.task.impl;

import com.florence.model.Entity;
import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.content.combat.AttackStyle;
import com.florence.model.content.combat.CombatHit;
import com.florence.model.content.combat.CombatType;
import com.florence.model.content.combat.WeaponAnimationTable;
import com.florence.model.content.combat.WeaponTimeTable;
import com.florence.model.player.Player;
import com.florence.task.Task;

public class PlayerToEntityCombatTask extends Task {

    /**
     * A dummy value for damage testing.
     */
    public static final int DEFAULT_DAMAGE = 1;

    private final Player player;
    private final Entity other;

    public PlayerToEntityCombatTask(Player player, Entity other) {
        super(player);
        this.player = player;
        this.other = other;
    }

    @Override
    public void execute() {
        if (player.getInteractingEntity() == null || other.getIndex() != player.getInteractingEntity().getIndex()) {
            stop();
        } else {
            final int distance = EntityCombatFollowingTask.DEFAULT_COMBAT_DISTANCE;
            if (player.getPosition().isWithinDistance(other.getPosition(), distance)) {
                final int modulus = WeaponTimeTable.getWeaponTime(player.getWeapon());
                if (ticks % modulus == 0) {
                    player.setAnimation(
                            WeaponAnimationTable.getAttackAnimationFor(player.getWeapon(), AttackStyle.ACCURATE));

                    other.setHitpoints(other.getHitpoints() - DEFAULT_DAMAGE);
                    other.setHit(new CombatHit(CombatType.MELEE, DEFAULT_DAMAGE));
                    other.getUpdateFlags().add(UpdateFlag.HIT_UPDATE);
                }
            }
        }
    }

    @Override
    public void cancel() {

    }
}
