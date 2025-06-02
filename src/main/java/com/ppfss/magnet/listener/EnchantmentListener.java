// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.listener;

import com.ppfss.magnet.service.MagnetEnchantService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class EnchantmentListener implements Listener {
    private final MagnetEnchantService enchantService;
    private final Enchantment magnetEnchantment;
    private final double chance;
    private final Random random = new Random();
    private final Logger logger;
    private final int maxLevel;

    public EnchantmentListener(Plugin plugin, MagnetEnchantService enchantService) {
        this.enchantService = enchantService;
        this.magnetEnchantment = enchantService.getMagnetEnchant();
        this.chance = plugin.getConfig().getDouble("enchant.table.chance", 10) / 100;
        this.logger = plugin.getLogger();
        this.maxLevel = magnetEnchantment.getMaxLevel();
    }



    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();

        if (!magnetEnchantment.canEnchantItem(item)) return;

        if (random.nextDouble() > chance) return;

        int enchantLevel = Math.min(event.getExpLevelCost() / 10 + 1, maxLevel);


        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
            if (meta == null)return;
        }

        if (item.getType() == Material.ENCHANTED_BOOK){
            ((EnchantmentStorageMeta) meta).addStoredEnchant(magnetEnchantment, enchantLevel, true);
        }else{
            meta.addEnchant(magnetEnchantment, enchantLevel, true);
        }

        enchantService.addMagnetEnchantLore(meta, enchantLevel);

        item.setItemMeta(meta);



        event.getEnchantsToAdd().put(magnetEnchantment, enchantLevel);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);
        ItemStack result = event.getResult();

        if (first == null || second == null) return;

        int firstMagnetLevel = enchantService.getMagnetLevel(first);
        int secondMagnetLevel = enchantService.getMagnetLevel(second);

        if (firstMagnetLevel == 0 && secondMagnetLevel == 0) return;

        if (first.getType() == Material.ENCHANTED_BOOK && second.getType() == Material.ENCHANTED_BOOK) {
            combineEnchantedBooks(first, second, result, event);
            return;
        }

        if (second.getType() == Material.ENCHANTED_BOOK) {
            combineBookToItem(first, second, result, event);
            return;
        }

        if (first.getType() == Material.ENCHANTED_BOOK)return;

        if (first.getType() != second.getType()) return;

        if (firstMagnetLevel ==  maxLevel || secondMagnetLevel ==  maxLevel) return;
        int resultMagnetLevel = combineLevel(firstMagnetLevel, secondMagnetLevel);


        Map<Enchantment, Integer> firstEnchants = first.getEnchantments();
        Map<Enchantment, Integer> secondEnchants = second.getEnchantments();

        if (result == null){
            if (firstEnchants.size() == 1 && firstMagnetLevel != 0){
                result = second.clone();
            }
            if (secondEnchants.size() == 1 && secondMagnetLevel != 0){
                result = first.clone();
            }
        }
        if (result == null) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        meta.addEnchant(magnetEnchantment, resultMagnetLevel, true);
        enchantService.addMagnetEnchantLore(meta, resultMagnetLevel);

        result.setItemMeta(meta);
        event.setResult(result);
    }

    private void combineBookToItem(ItemStack first, ItemStack second, ItemStack result, PrepareAnvilEvent event) {
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) second.getItemMeta();
        if (bookMeta == null) return;

        if (!magnetEnchantment.canEnchantItem(first))return;

        int itemMagnetLevel = enchantService.getMagnetLevel(first);
        int bookMagnetLevel = enchantService.getMagnetLevel(second);
        int resultMagnetLevel = combineLevel(bookMagnetLevel, itemMagnetLevel);

        if (resultMagnetLevel == 0) return;


        if (result == null){
            if (bookMeta.getStoredEnchants().size() == 1 && bookMagnetLevel == 0) return;
            result = first.clone();
        }

        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(result.getType());
            if (meta == null) return;
        }

        meta.addEnchant(magnetEnchantment, resultMagnetLevel, true);
        enchantService.addMagnetEnchantLore(meta, resultMagnetLevel);

        result.setItemMeta(meta);
        event.setResult(result);
    }

    private void combineEnchantedBooks(ItemStack first, ItemStack second, ItemStack result, PrepareAnvilEvent event) {
        EnchantmentStorageMeta firstBookMeta = (EnchantmentStorageMeta) first.getItemMeta();
        EnchantmentStorageMeta secondBookMeta = (EnchantmentStorageMeta) second.getItemMeta();
        if (firstBookMeta == null || secondBookMeta == null) return;
        Map<Enchantment, Integer> firstEnchants = firstBookMeta.getStoredEnchants();
        Map<Enchantment, Integer> secondEnchants = secondBookMeta.getStoredEnchants();


        int firstMagnetLevel = enchantService.getMagnetLevel(first);
        int secondMagnetLevel = enchantService.getMagnetLevel(second);
        if (firstMagnetLevel == maxLevel || secondMagnetLevel == maxLevel)return;

        int resultMagnetLevel = combineLevel(firstMagnetLevel, secondMagnetLevel);
        if (resultMagnetLevel == 0) return;

        if (result == null){
            if (firstEnchants.size() == 1 && firstMagnetLevel != 0) {
                result = second.clone();
            }
            if (secondEnchants.size() == 1 && secondMagnetLevel != 0) {
                result = first.clone();
            }
        }

        if (result == null)return;

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
        if (meta == null) return;

        meta.addEnchant(magnetEnchantment, resultMagnetLevel, true);
        enchantService.addMagnetEnchantLore(meta, resultMagnetLevel);

        result.setItemMeta(meta);
        event.setResult(result);
    }

    private int combineLevel(int firstLevel, int secondLevel){
        if (firstLevel == 0 && secondLevel == 0) return 0;
        if (firstLevel == secondLevel){
            return firstLevel+1;
        }
        return Math.max(firstLevel, secondLevel);
    }

}
