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
package com.florence.model.content;

import com.florence.model.Animation;
import com.florence.model.player.Player;

public class EmoteButtons {

    public enum Emotes {
        DANCE(866, 166),
        CLAP(865, 172),
        BECKON(864, 167),
        WAVE(863, 163),
        CHEER(862, 171),
        LAUGH(861, 170),
        CRY(860, 161),
        ANGRY(859, 165),
        BOW(858, 164),
        THINK(857, 162),
        NO(856, 169),
        YES(855, 168);

        private int animation;
        private int button;

        Emotes(int animation, int button) {
            this.animation = animation;
            this.button = button;
        }

        public int getAnimation() {
            return animation;
        }

        public void setAnimation(int animation) {
            this.animation = animation;
        }

        public int getButton() {
            return button;
        }

        public void setButton(int button) {
            this.button = button;
        }
    }

    public static boolean pressed(int button, Player player) {
        for (final Emotes emote : Emotes.values()) {
            if (emote.getButton() == button) {
                player.setAnimation(new Animation(emote.getAnimation(), 0));
                return true;
            }
        }
        return false;
    }
}
