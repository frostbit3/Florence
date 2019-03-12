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
package com.florence.net.packet.decoders;

import com.florence.model.UpdateFlags.UpdateFlag;
import com.florence.model.World;
import com.florence.model.mob.Mob;
import com.florence.model.player.Player;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoder;
import com.florence.task.impl.EntityCombatFollowingTask;
import com.florence.task.impl.PlayerToEntityCombatTask;

public class AttackMobPacketDecoder implements PacketDecoder {

    @Override
    public void decode(Packet packet, Player player) {
        final int index = packet.readShortA();

        /**
         * We're already in combat with this non-player character.
         */
        if (player.getInteractingEntity() != null && index == player.getInteractingEntity().getIndex())
            return;

        final Mob victim = World.singleton().getMobs().get(index);
        if (victim == null)
            return;

        /**
         * Sets the victim.
         */
        player.setInteractingEntity(victim);

        /**
         * Faces the victim.
         */
        player.getUpdateFlags().add(UpdateFlag.FACE_ENTITY_UPDATE);

        /**
         * Will continuously attempt to find the shortest route to the victim.
         */
        World.singleton().schedule(new EntityCombatFollowingTask(player, victim));

        /**
         * Will continuously attempt to attack the victim if they're within
         * range.
         */
        World.singleton().schedule(new PlayerToEntityCombatTask(player, victim));
    }
}
