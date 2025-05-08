// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.listener;

import com.ppfss.magnet.cache.MagnetCache;
import com.ppfss.magnet.service.MagnetEnchantService;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class EnchantmentListener implements Listener {
    private final MagnetCache magnetCache;
    private final MagnetEnchantService enchantService;
    private final Enchantment magnetEnchantment;
    private double chance;
    private final Random random = new Random();
    private final Logger logger;

    public EnchantmentListener(Plugin plugin, MagnetCache magnetCache, MagnetEnchantService enchantService) {
        this.magnetCache = magnetCache;
        this.enchantService = enchantService;
        this.magnetEnchantment = enchantService.getMagnetEnchant();
        this.chance = plugin.getConfig().getDouble("enchant.chance", 10) / 100;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();

        if (!magnetEnchantment.canEnchantItem(item)) return;

        if (random.nextDouble() >= chance) return;

        int enchantLevel = Math.min(event.getExpLevelCost() / 10 + 1, magnetEnchantment.getMaxLevel());
        if (!item.hasItemMeta()) return;

        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }

        item = enchantService.enchantMagnet(item, enchantLevel, true);



        event.getEnchantsToAdd().put(magnetEnchantment, enchantLevel);
        logger.info("Enchant successfully added");
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);
        ItemStack result = event.getResult();

        if (first == null || second == null) return;

        if (!magnetEnchantment.canEnchantItem(first)) return;


        boolean isFirstMagnet = enchantService.hasEnchant(first);
        boolean isSecondMagnet = enchantService.hasEnchant(second);

        if (!isFirstMagnet && !isSecondMagnet) return;

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

        Map<Enchantment, Integer> firstEnchants = first.getEnchantments();
        Map<Enchantment, Integer> secondEnchants = second.getEnchantments();

        if (result == null){
            if (firstEnchants.size() != 1 && secondEnchants.size() != 1){
                return;
            }
            result = first.clone();
        }



        for (Map.Entry<Enchantment, Integer> entry : firstEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();

            int level = entry.getValue();
            int secondLevel = secondEnchants.get(enchantment);

            level = combineLevel(level, secondLevel);

            if (level > enchantment.getMaxLevel()){
                level = enchantment.getMaxLevel();
            }

            result.addUnsafeEnchantment(enchantment, level);
        }

        event.setResult(result);
    }

    private void combineBookToItem(ItemStack first, ItemStack second, ItemStack result, PrepareAnvilEvent event) {
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) second.getItemMeta();
        if (bookMeta == null) return;

        int firstLevel = first.getEnchantmentLevel(magnetEnchantment);
        int secondLevel = bookMeta.getStoredEnchantLevel(magnetEnchantment);

        int resultLevel = combineLevel(firstLevel, secondLevel);
        if (resultLevel > magnetEnchantment.getMaxLevel()) {
            resultLevel = magnetEnchantment.getMaxLevel();
        }


        if (bookMeta.getStoredEnchants().size() == 1) {
            if (result == null){
                result = first.clone();
            }
        }else{
            if (result == null) return;

            Map<Enchantment, Integer> enchants = bookMeta.getStoredEnchants();
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                if (entry.getKey() == magnetEnchantment) {
                    continue;
                }
                Enchantment enchantment = entry.getKey();

                int itemLevel = result.getEnchantmentLevel(enchantment);

                if (!enchantment.canEnchantItem(result)) continue;
                int level = combineLevel(entry.getValue(), itemLevel);

                if (level > enchantment.getMaxLevel()){
                    level = enchantment.getMaxLevel();
                }
565862,,3
                result.addUnsafeEnchantment(enchantment, level);
            }
        }

        result.addUnsafeEnchantment(magnetEnchantment, resultLevel);

        event.setResult(result);
        return;
    }

    private void combineEnchantedBooks(ItemStack first, ItemStack second, ItemStack result, PrepareAnvilEvent event) {
        EnchantmentStorageMeta firstMeta = (EnchantmentStorageMeta) first.getItemMeta();
        EnchantmentStorageMeta secondMeta = (EnchantmentStorageMeta) second.getItemMeta();

        if (firstMeta == null|| secondMeta == null)return;

        int firstEnchantLevel = firstMeta.getStoredEnchantLevel(magnetEnchantment);
        int secondEnchantLevel = secondMeta.getStoredEnchantLevel(magnetEnchantment);

        int resultLevel = combineLevel(firstEnchantLevel, secondEnchantLevel);

        if (resultLevel > magnetEnchantment.getMaxLevel()){
            resultLevel = magnetEnchantment.getMaxLevel();
        }

        EnchantmentStorageMeta resultMeta;

        if (result == null || result.getType().isAir()){
            result = first.clone();
            resultMeta = (EnchantmentStorageMeta) first.getItemMeta();
            if (resultMeta == null) return;

            Map<Enchantment, Integer> firstEnchantments = firstMeta.getStoredEnchants();
            Map<Enchantment, Integer> secondEnchantments = secondMeta.getStoredEnchants();

            for (Map.Entry<Enchantment, Integer> entry : secondEnchantments.entrySet()) {
                int firstLevel = firstEnchantments.get(entry.getKey());
                if (entry.getKey() == magnetEnchantment) continue;

                Enchantment enchantment = entry.getKey();

                int level = combineLevel(firstLevel, entry.getValue());
                if (level == 0)continue;
                if (level > enchantment.getMaxLevel()){
                    level = enchantment.getMaxLevel();
                }

                resultMeta.addStoredEnchant(enchantment, level, true);
            }
        }else {
            resultMeta = (EnchantmentStorageMeta) result.getItemMeta();
            if (resultMeta == null) return;
        }

        resultMeta.addStoredEnchant(magnetEnchantment, resultLevel, true);
        result.setItemMeta(resultMeta);

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
