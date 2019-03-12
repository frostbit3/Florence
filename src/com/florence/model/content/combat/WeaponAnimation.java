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

public class WeaponAnimation {

    private final AttackStyle style;
    private final Animation animation;

    public WeaponAnimation(AttackStyle style, Animation animation) {
        this.style = style;
        this.animation = animation;
    }

    public AttackStyle getStyle() {
        return style;
    }

    public Animation getAnimation() {
        return animation;
    }
}
