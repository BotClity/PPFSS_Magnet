// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.task;

import com.ppfss.magnet.config.Config;
import com.ppfss.magnet.model.MagnetData;
import com.ppfss.magnet.model.ParticleData;
import com.ppfss.magnet.service.MagnetService;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

public class MagnetRunnable extends BukkitRunnable {
    private final MagnetService magnetService;
    private final Function<Player, Boolean> hasMagnet;
    @Setter
    private ParticleData particleData;

    public MagnetRunnable(MagnetService magnetService, Function<Player, Boolean> hasMagnet) {
        this.magnetService = magnetService;
        this.hasMagnet = hasMagnet;
        this.particleData = Config.getInstance().getParticleData();
    }

    @Override
    public void run() {
        Map<UUID, MagnetData> active = magnetService.getActiveMagnets();

        active.forEach((uuid, data) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                magnetService.removeActiveMagnet(uuid);
                return;
            }

            if (!hasMagnet.apply(player)) {
                magnetService.removeActiveMagnet(uuid);
                return;
            }

            moveAllNearbyItems(player, data);
        });
    }

    private void moveAllNearbyItems(@NotNull Player player, @NotNull MagnetData data) {
        Location playerLocation = player.getLocation();
        Location destination = playerLocation.add(0, 1, 0);
        double teleport_distance = 0.6;
        int radius = data.radius();
        double strength = data.strength();
        int limit = data.limit();
        if (limit == 0) limit = Integer.MAX_VALUE;

        Stream<Item> items = player.getNearbyEntities(radius, radius, radius)
                .stream()
                .filter(entity -> entity instanceof Item)
                .map(e -> (Item) e)
                .limit(limit);

        items.forEach(item ->{
            double distance = item.getLocation().distance(playerLocation);

            if (distance > radius) return;

            if (distance < teleport_distance){
                item.teleport(destination);
                return;
            }

            Vector direction = playerLocation.toVector()
                    .subtract(item.getLocation().toVector())
                    .normalize();

            item.setVelocity(direction.multiply(strength));

            if (particleData.isEnabled()){
                player.spawnParticle(
                        particleData.getType(),
                        item.getLocation(),
                        3,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }
        });
    }
}
