// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.task;

import com.ppfss.magnet.cache.MagnetCache;
import com.ppfss.magnet.domain.MagnetData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public MagnetTask(MagnetCache cache, long delay, long period) {
        this.cache = cache;
        this.delay = delay;
        this.period = period;
    }

    @Override
    public void run() {
        Map<UUID, MagnetData> activeMagnets = cache.getActiveMagnets();

        List<Player> players = activeMagnets.keySet()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();

        for (Player player : players) {
            Location loc = player.getLocation();
            MagnetData data = activeMagnets.get(player.getUniqueId());
            int radius = data.radius();


            List<Item> items = player.getNearbyEntities(radius, radius, radius)
                    .stream()
                    .filter(e -> e instanceof Item)
                    .map(e -> (Item) e)
                    .toList();

            moveItems(loc, items, data);
        }
    }

    private void moveItems(Location location, List<Item> items, MagnetData data){
        for (Item item : items) {
            double distance = item.getLocation().distance(location);

            if (distance > data.radius() || distance < 0.6){
                continue;
            }

            Vector vector = location.toVector().subtract(item.getLocation().toVector());
            item.setVelocity(vector.multiply(data.strength()));
        }


    }

//    private void initGlobalTask() {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//
//
//                for (UUID uuid : players.keySet()) {
//                    Player player = Bukkit.getPlayer(uuid);
//                    if (player == null || !player.isOnline()) {
//                        removePlayer(uuid);
//                        continue;
//                    }
//
//                    if (config.shiftRequired && !player.isSneaking()) {
//                        continue;
//                    }
//
//                    int radius = players.getOrDefault(uuid, config.defaultRadius);
//                    List<Item> nearbyItems = player.getNearbyEntities(radius, radius, radius).stream()
//                            .filter(e -> e instanceof Item)
//                            .map(e -> (Item) e)
//                            .toList();
//
//                    for (Item item : nearbyItems) {
//                        magnetItems.put(item, new MagnetData(uuid, radius));
//                    }
//                }
//
//                moveAllMagnetItems();
//            }
//        }.runTaskTimer(plugin, 0L, 20L);
//    }
//
//    private void moveAllMagnetItems() {
//        for (Iterator<Map.Entry<Item, MagnetData>> it = magnetItems.entrySet().iterator(); it.hasNext(); ) {
//            Map.Entry<Item, MagnetData> entry = it.next();
//            Item item = entry.getKey();
//            MagnetData data = entry.getValue();
//
//            Player owner = Bukkit.getPlayer(data.owner());
//            if (owner == null || !owner.isOnline() || !item.isValid()) {
//                it.remove();
//                continue;
//            }
//
//            ItemStack mainHand = owner.getInventory().getItemInMainHand();
//            ItemStack offHand = owner.getInventory().getItemInOffHand();
//            ItemStack helmet = owner.getInventory().getHelmet();
//
//            boolean isActiveMagnet = isEnabledAndValid(mainHand, MagnetType.RIGHTHAND) ||
//                    isEnabledAndValid(offHand, MagnetType.LEFTHAND) ||
//                    isEnabledAndValid(helmet, MagnetType.HEAD);
//
//            if (!isActiveMagnet) {
//                it.remove();
//                continue;
//            }
//
//            Location itemLoc = item.getLocation();
//            Location playerLoc = owner.getLocation().add(0, 1, 0);
//            double distance = itemLoc.distance(playerLoc);
//
//            if (distance > data.radius() || distance < 0.6) {
//                it.remove();
//                continue;
//            }
//
//            Vector direction = playerLoc.toVector().subtract(itemLoc.toVector()).normalize();
//            item.setVelocity(direction.multiply(config.defaultSpeed));
//        }
//    }
}
