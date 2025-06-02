// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.task;

import com.ppfss.magnet.cache.MagnetCache;
import com.ppfss.magnet.domain.MagnetData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class MagnetTask extends BukkitRunnable {
    private final MagnetCache cache;
    @Getter
    private final long delay;
    @Getter
    private final long period;
    private final Particle particle;
    @Getter
    @Setter
    private boolean stopped = false;

    public MagnetTask(MagnetCache cache, long delay, long period, Particle particle) {
        this.cache = cache;
        this.particle = particle;
        this.delay = delay;
        this.period = period;
    }

    @Override
    public void run() {
        if (stopped)return;
        Map<UUID, Integer> activeMagnets = cache.getActiveMagnets();

        List<Player> players = activeMagnets.keySet()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();


        for (Player player : players) {
            Location loc = player.getLocation();
            int level = activeMagnets.get(player.getUniqueId());
            MagnetData data = cache.getMagnetLevel(level);
            double radius = data.radius();


            int limit = data.limit();
            if (limit == 0)limit = 100;

            List<Item> items = player.getNearbyEntities(radius, radius, radius)
                    .stream()
                    .filter(e -> e instanceof Item)
                    .map(e -> (Item) e)
                    .limit(limit)
                    .toList();

            moveItems(loc, items, data, player);
        }
    }

    private void moveItems(Location location, List<Item> items, MagnetData data, Player player) {
        double slowRadius = data.radius() * 0.3;
        double teleportDistance = 0.6;

        for (Item item : items) {
            double distance = item.getLocation().distance(location);

            if (distance > data.radius()) continue;

            if (distance <= teleportDistance) {
                Location teleportLoc = location.clone().add(0, 1, 0);
                item.teleport(teleportLoc);
                continue;
            }

            Vector direction = location.toVector()
                    .subtract(item.getLocation().toVector())
                    .normalize();


            item.setVelocity(direction.multiply(data.speed()));

            if (particle != null) {
                player.spawnParticle(
                        particle,
                        item.getLocation(),
                        3,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }
        }
    }

}
