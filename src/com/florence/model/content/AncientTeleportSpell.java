/*
 * Copyright (C) 2019 Dylan Vicchiarelli
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.florence.model.content;

import com.florence.model.Animation;
import com.florence.model.Entity;
import com.florence.model.Graphic;
import com.florence.model.player.Player;
import com.florence.model.player.Player.RegionState;
import com.florence.model.Position;
import com.florence.task.Task;

public class AncientTeleportSpell extends TeleportSpell {

    public static final Animation ANCIENT_TELEPORT_ANIMATION = Animation.create(1979, 0);
    public static final Graphic ANCIENT_TELEPORT_GRAPHIC = Graphic.create(392, 0, 0);
    public static final Animation ANCIENT_ENDING_ANIMATION = Animation.create(65535, 0);

    public AncientTeleportSpell(Position destination) {
        super(destination, ANCIENT_TELEPORT_ANIMATION, ANCIENT_TELEPORT_GRAPHIC);
    }

    @Override
    public Task cast(Entity entity) {
        entity.setTeleported(true);
        entity.setAnimation(animation);
        return new Task(entity) {
            @Override
            public void execute() {
                if (ticks == 2) {
                    entity.setGraphic(graphic);
                } else if (ticks == 5) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.setRegionState(RegionState.REBUILDING_REGION);
                    }
                    entity.setPosition(destination);
                    stop();
                }
            }

            @Override
            public void cancel() {
                entity.setAnimation(ANCIENT_ENDING_ANIMATION);
                entity.setTeleported(false);
            }
        };
    }
}
