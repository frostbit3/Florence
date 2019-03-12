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
package com.florence.model;

import com.florence.model.player.LogoutRequest;
import com.florence.model.player.LoginRequest;
import com.florence.model.mob.Mob;
import com.florence.model.player.Player;
import com.florence.model.item.ItemDefinitions;
import com.florence.model.mob.StaticMobsDocumentParser;
import com.florence.model.update.UpdateServiceListener;
import com.florence.model.update.impl.MainPlayerUpdateService;
import com.florence.model.update.impl.PostMobUpdateService;
import com.florence.model.update.impl.PostPlayerUpdateService;
import com.florence.model.update.impl.PreMobUpdateService;
import com.florence.model.update.impl.PrePlayerUpdateService;
import com.florence.net.packet.Packet;
import com.florence.net.packet.PacketDecoderTable;
import com.florence.task.Task;
import com.florence.task.TaskFactory;
import com.florence.task.impl.RestoreRunEnergyTask;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class World implements Runnable {

    public static final int THREAD_RATE = 600;
    public static final int MAXIMUM_PLAYERS = 2000;
    public static final int MAXIMUM_MOBS = 8192;

    private final PacketDecoderTable packets = new PacketDecoderTable();
    private final EntityRegistry<Player> players = new EntityRegistry<>(MAXIMUM_PLAYERS);
    private final EntityRegistry<Mob> mobs = new EntityRegistry<>(MAXIMUM_MOBS);

    private final TaskFactory tasks = new TaskFactory();
    private final UpdateServiceListener<Player> player_updates = new UpdateServiceListener(players);
    private final UpdateServiceListener<Mob> mob_updates = new UpdateServiceListener(mobs);

    /**
     * Schedules commands to run after a given delay, or to execute
     * periodically.
     */
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors());

    /**
     * An unbounded thread-safe queue based on linked nodes. Stores user
     * requests to be registered in the physical world.
     */
    private final ConcurrentLinkedQueue<LoginRequest> logins = new ConcurrentLinkedQueue<>();

    /**
     * An unbounded thread-safe queue based on linked nodes. Stores user
     * requests to be removed from the physical world.
     */
    private final ConcurrentLinkedQueue<LogoutRequest> logouts = new ConcurrentLinkedQueue<>();

    /**
     * Provides a global access point and ensures that only one instance of this
     * class file exists within the virtual machine.
     */
    private static World singleton;

    public static World singleton() {
        if (singleton == null)
            singleton = new World();
        return singleton;
    }

    public ScheduledFuture<?> initialize() throws Exception {
        ItemDefinitions.load();
        StaticMobsDocumentParser.load();

        player_updates.register(PrePlayerUpdateService.class, new PrePlayerUpdateService());
        player_updates.register(MainPlayerUpdateService.class, new MainPlayerUpdateService());
        player_updates.register(PostPlayerUpdateService.class, new PostPlayerUpdateService());

        mob_updates.register(PreMobUpdateService.class, new PreMobUpdateService());
        mob_updates.register(PostMobUpdateService.class, new PostMobUpdateService());

        tasks.schedule(new RestoreRunEnergyTask(World.class));
        return service.scheduleAtFixedRate(this, 0, THREAD_RATE, TimeUnit.MILLISECONDS);
    }

    public boolean contains(String username) {
        for (Player player : players) {
            if (player == null)
                continue;
            if (player.getUsername().equalsIgnoreCase(username))
                return true;
        }
        return false;
    }

    @Override
    public void run() {
        LoginRequest login;
        while ((login = logins.poll()) != null) {
            login.login();
        }

        LogoutRequest logout;
        while ((logout = logouts.poll()) != null) {
            logout.logout();
        }

        final Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            final Player player = iterator.next();

            Packet packet;
            while ((packet = player.getPackets().poll()) != null) {
                packets.decode(player, packet);
            }
        }

        mob_updates.execute(PreMobUpdateService.class);
        player_updates.execute(PrePlayerUpdateService.class);

        tasks.run();

        player_updates.execute(MainPlayerUpdateService.class);

        mob_updates.execute(PostMobUpdateService.class);
        player_updates.execute(PostPlayerUpdateService.class);
    }

    public void schedule(Task task) {
        tasks.schedule(task);
    }

    public PacketDecoderTable getPackets() {
        return packets;
    }

    public TaskFactory getTasks() {
        return tasks;
    }

    public ConcurrentLinkedQueue<LoginRequest> getLogins() {
        return logins;
    }

    public EntityRegistry<Player> getPlayers() {
        return players;
    }

    public ConcurrentLinkedQueue<LogoutRequest> getLogouts() {
        return logouts;
    }

    public EntityRegistry<Mob> getMobs() {
        return mobs;
    }
}
