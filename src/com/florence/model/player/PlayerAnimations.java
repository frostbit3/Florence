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
package com.florence.model.player;

import com.florence.model.Animation;

public class PlayerAnimations {

    public static final Animation DEFAULT_WALK = Animation.create(819, 0);
    public static final Animation DEFAULT_RUN = Animation.create(824, 0);
    public static final Animation DEFAULT_IDLE = Animation.create(808, 0);

    public static final Animation DEFAULT_TURNING = Animation.create(823, 0);
    public static final Animation DEFAULT_BACKTRACK = Animation.create(820, 0);
    public static final Animation DEFAULT_SIDESTEP_A = Animation.create(821, 0);
    public static final Animation DEFAULT_SIDESTEP_B = Animation.create(822, 0);

    private Animation standing;
    private Animation walking;
    private Animation running;

    public PlayerAnimations() {
        this(DEFAULT_IDLE, DEFAULT_WALK, DEFAULT_RUN);
    }

    public PlayerAnimations(Animation standing, Animation walking, Animation running) {
        this.standing = standing;
        this.walking = walking;
        this.running = running;
    }

    public Animation getWalking() {
        return walking;
    }

    public void setWalking(Animation walking) {
        this.walking = walking;
    }

    public Animation getRunning() {
        return running;
    }

    public void setRunning(Animation running) {
        this.running = running;
    }

    public Animation getStanding() {
        return standing;
    }

    public void setStanding(Animation standing) {
        this.standing = standing;
    }
}
