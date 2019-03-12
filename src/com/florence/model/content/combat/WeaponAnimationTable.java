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

import com.florence.model.Animation;
import com.florence.model.player.PlayerAnimations;

public enum WeaponAnimationTable {

    UNARMED(new int[]{-1}, PlayerAnimations.DEFAULT_IDLE, PlayerAnimations.DEFAULT_WALK, PlayerAnimations.DEFAULT_RUN,
            new WeaponAnimation[]{
                new WeaponAnimation(AttackStyle.ACCURATE, Animation.create(422, 0)),
                new WeaponAnimation(AttackStyle.CONTROLLED, Animation.create(423, 0)),
                new WeaponAnimation(AttackStyle.DEFENSIVE, Animation.create(422, 0))
            }, new Animation(0, 424)),
    ABYSALL_WHIP(new int[]{4151},
            Animation.create(1832, 0), Animation.create(1660, 0), Animation.create(1661, 0),
            new WeaponAnimation[]{
                new WeaponAnimation(AttackStyle.ACCURATE, Animation.create(1658, 0)),
                new WeaponAnimation(AttackStyle.CONTROLLED, Animation.create(1658, 0)),
                new WeaponAnimation(AttackStyle.DEFENSIVE, Animation.create(1658, 0))}, Animation.create(1659, 0)),
    GRANITE_MAUL(new int[]{4153},
            Animation.create(1662, 0), Animation.create(1663, 0), Animation.create(1664, 0),
            new WeaponAnimation[]{
                new WeaponAnimation(AttackStyle.ACCURATE, Animation.create(1665, 0)),
                new WeaponAnimation(AttackStyle.CONTROLLED, Animation.create(1665, 0)),
                new WeaponAnimation(AttackStyle.DEFENSIVE, Animation.create(1665, 0))}, Animation.create(1666, 0));

    private final int[] indexes;
    private final Animation stand;
    private final Animation walk;
    private final Animation run;
    private final WeaponAnimation[] attack;
    private final Animation defend;

    WeaponAnimationTable(int[] indexes, Animation stand, Animation walk, Animation run, WeaponAnimation[] attack, Animation defend) {
        this.indexes = indexes;
        this.stand = stand;
        this.walk = walk;
        this.run = run;
        this.attack = attack;
        this.defend = defend;
    }

    public static Animation getAttackAnimationFor(int weapon, AttackStyle style) {
        for (WeaponAnimationTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (entry.getIndexes()[index] == weapon) {
                    for (WeaponAnimation animation : entry.getAttackAnimations()) {
                        if (style == animation.getStyle()) {
                            return animation.getAnimation();
                        }
                    }
                }
            }
        }
        return PlayerAnimations.DEFAULT_IDLE;
    }

    public static Animation getStandAnimationFor(int weapon) {
        for (WeaponAnimationTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (entry.getIndexes()[index] == weapon) {
                    return entry.getStandAnimation();
                }
            }
        }
        return PlayerAnimations.DEFAULT_IDLE;
    }

    public static Animation getWalkAnimationFor(int weapon) {
        for (WeaponAnimationTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (entry.getIndexes()[index] == weapon) {
                    return entry.getWalkAnimation();
                }
            }
        }
        return PlayerAnimations.DEFAULT_WALK;
    }

    public static Animation getRunAnimationFor(int weapon) {
        for (WeaponAnimationTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (entry.getIndexes()[index] == weapon) {
                    return entry.getRunAnimation();
                }
            }
        }
        return PlayerAnimations.DEFAULT_RUN;
    }

    public static Animation getDefendAnimationFor(int weapon) {
        for (WeaponAnimationTable entry : values()) {
            for (int index = 0; index < entry.getIndexes().length; index++) {
                if (entry.getIndexes()[index] == weapon) {
                    return entry.getDefendAnimation();
                }
            }
        }
        return PlayerAnimations.DEFAULT_IDLE;
    }

    public Animation getStandAnimation() {
        return stand;
    }

    public Animation getWalkAnimation() {
        return walk;
    }

    public Animation getRunAnimation() {
        return run;
    }

    public WeaponAnimation[] getAttackAnimations() {
        return attack;
    }

    public Animation getDefendAnimation() {
        return defend;
    }

    public int[] getIndexes() {
        return indexes;
    }
}
