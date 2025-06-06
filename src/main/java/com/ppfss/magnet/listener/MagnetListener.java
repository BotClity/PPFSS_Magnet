// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.listener;

import com.google.common.collect.Lists;
import com.ppfss.magnet.cache.MagnetCache;
import com.ppfss.magnet.service.MagnetEnchantService;
import com.ppfss.magnet.utils.ColorUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class MagnetListener implements Listener {
    private final Plugin plugin;
    private final MagnetCache cache;
    private final MagnetEnchantService enchantService;
    private final TextComponent activationComponent;
    private final TextComponent deactivationComponent;

    public MagnetListener(Plugin plugin, MagnetCache cache, MagnetEnchantService enchantService, ConfigurationSection section) {
        this.plugin = plugin;
        this.cache = cache;
        this.enchantService = enchantService;
        String activation = ColorUtils.color(section.getString("magnet.activation", "Магнит активирован!"));
        String deactivation = ColorUtils.color(section.getString("magnet.deactivation", "Магнит отключён!"));
        this.activationComponent = new TextComponent(activation);
        this.deactivationComponent = new TextComponent(deactivation);
    }


    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();
        ItemStack previous = inventory.getItem(event.getPreviousSlot());
        ItemStack current = inventory.getItem(event.getNewSlot());



        int level = getMagnet(current);
        updateMagnet(player, level);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.isCancelled())return;


        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerInventory inventory = player.getInventory();
                int level = getMagnet(inventory);

                updateMagnet(player, level);
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Integer activeLevel = cache.getActiveMagnet(player.getUniqueId());
        if (activeLevel == null) return;

        PlayerInventory inventory = player.getInventory();

        boolean magnetize = containsMagnet(inventory);

        if (magnetize) return;

        deactivateMagnet(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        deactivateMagnet(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        deactivateMagnet(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerInventory inventory = player.getInventory();
                int level = getMagnet(inventory);

                if (level != 0){
                    activateMagnet(player, level);
                }
            }
        }.runTask(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!isMagnet(item))return;
        if (!isArmor(item)) return;

        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerInventory inventory = player.getInventory();
                int level = getMagnet(inventory, item);
                updateMagnet(player, level);
            }
        }.runTask(plugin);
    }


    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event){
        ItemStack item;

        if (event.getHand() == EquipmentSlot.HAND){
            item = event.getItemInHand();
        }else{
            item = event.getPlayer().getInventory().getItemInOffHand();
        }


        if (item.getType().isAir()) return;
        if (!isMagnet(item)) return;
        event.setCancelled(true);
    }

    private boolean isArmor(ItemStack item){
        String type = item.getType().name();

        return type.endsWith("HELMET") || type.endsWith("CHESTPLATE") || type.endsWith("LEGGINGS") || type.endsWith("BOOTS") || type.equals("PLAYER_HEAD");
    }


    private void updateMagnet(Player player, int level){
        Integer activeLevel = cache.getActiveMagnet(player.getUniqueId());


        if (activeLevel != null && activeLevel != 0){
            if (activeLevel == level) return;
            deactivateMagnet(player);
            if (level == 0) return;
            activateMagnet(player, level);
            return;
        }
        if (level == 0)return;

        activateMagnet(player, level);
    }

    private int getMagnet(ItemStack item){
        if (item == null || item.getType().isAir()) return 0;
        return enchantService.getMagnetLevel(item);
    }

    private int getMagnet(PlayerInventory inventory, ItemStack... args) {
        List<ItemStack> items = Lists.newArrayList(
                inventory.getItemInOffHand(),
                inventory.getItemInMainHand(),
                inventory.getHelmet(),
                inventory.getChestplate(),
                inventory.getLeggings(),
                inventory.getBoots()
        );

        items.addAll(Arrays.asList(args));

        int maxLevel = 0;

        for (ItemStack item : items) {
            if (item == null || item.getType().isAir())continue;
            maxLevel = Math.max(maxLevel, getMagnet(item));
        }

        return maxLevel;
    }


    private boolean containsMagnet(PlayerInventory inventory) {
        ItemStack[] equipment = {
                inventory.getItemInOffHand(),
                inventory.getItemInMainHand(),
                inventory.getHelmet(),
                inventory.getChestplate(),
                inventory.getLeggings(),
                inventory.getBoots()
        };

        for (ItemStack item : equipment) {
            if (isMagnet(item)) return true;
        }

        return false;
    }

    private boolean isMagnet(ItemStack item) {
        if (item == null) return false;
        if (item.getType().isAir()) return false;
        return enchantService.isMagnet(item);
    }

    private void deactivateMagnet(Player player) {
        cache.removeActiveMagnet(player.getUniqueId());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, deactivationComponent);
    }

    private void activateMagnet(Player player, int level) {
        cache.addActiveMagnet(player.getUniqueId(), level);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, activationComponent);
    }
}
