// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.listener;

import com.ppfss.magnet.service.MagnetService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MagnetListener implements Listener {
    private final MagnetService magnetService;
    private final Plugin plugin;

    public MagnetListener(MagnetService magnetService, Plugin plugin) {
        this.magnetService = magnetService;
        this.plugin = plugin;
    }

    private void verify(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                magnetService.verifyPlayer(player);
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler
    private void onItemHeld(PlayerItemHeldEvent event) {
        verify(event.getPlayer());
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        verify(player);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        verify(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        magnetService.removeActiveMagnet(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        verify(event.getPlayer());
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {
        magnetService.removeActiveMagnet(event.getEntity().getUniqueId());
    }

    @EventHandler
    private void dropItem(PlayerDropItemEvent event) {
        verify(event.getPlayer());
    }

    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        if (event.getHand() != EquipmentSlot.HAND) {
            item = event.getPlayer().getInventory().getItemInOffHand();
        }

        if (item.getType().isAir())return;
        if (magnetService.getMagnetData(item) == null) return;
        event.setCancelled(true);
    }
}
