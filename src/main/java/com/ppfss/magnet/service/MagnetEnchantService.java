package com.ppfss.magnet.service;

import com.ppfss.magnet.domain.MagnetEnchantment;
import com.ppfss.magnet.utils.ColorUtils;
import com.ppfss.magnet.utils.EnchantmentRegister;
import com.ppfss.magnet.utils.LogUtils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MagnetEnchantService {
    private static final String NBT_KEY = "magnet_enchant";
    private final NamespacedKey magnetKey;
    private final Enchantment magnetEnchant;
    private final String loreFormat;


    public MagnetEnchantService(Plugin plugin) {
        magnetKey = new NamespacedKey(plugin, "magnet");
        FileConfiguration cfg = plugin.getConfig();

        loreFormat = cfg.getString("enchant.lore-format", "&7Магнит %level%").replace('&', '§');

        String name = cfg.getString("enchant.name", "Магнит");

        int maxLevel = cfg.getInt("enchant.max-level", 3);
        List<String> allowedEnchants = cfg.getStringList("enchant.allowed-items");

        magnetEnchant = new MagnetEnchantment(magnetKey, ColorUtils.color(name), maxLevel, allowedEnchants);

        EnchantmentRegister.registerEnchantments(magnetEnchant);
    }

    public ItemStack removeMagnet(ItemStack item) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

            if (meta == null)return item;

            meta.removeStoredEnchant(magnetEnchant);
            item.setItemMeta(meta);
            return item;
        }
        ItemMeta meta = item.getItemMeta();

        if (meta == null)return item;

        meta.removeEnchant(magnetEnchant);
        item.setItemMeta(meta);

        return item;
    }

    public boolean isMagnet(ItemStack item){
        Map<Enchantment, Integer> enchants;


        if (item.getType() == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta == null)return false;

            enchants = meta.getStoredEnchants();
        }else{
            enchants = item.getEnchantments();
        }

        for (Enchantment enchant: enchants.keySet()){
            if (enchant == magnetEnchant) return true;
        }
        return false;
    }

    public ItemStack enchantMagnet(ItemStack item, int level, boolean forced){
        if (item.getType() == Material.ENCHANTED_BOOK){
            return enchantBook(item, level, forced);
        }
        return enchantItem(item, level, forced);
    }

    private ItemStack enchantItem(ItemStack item, int level, boolean forced){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (forced){
            meta.addEnchant(magnetEnchant, level, true);
        }else{
            int oldLevel = meta.getEnchantLevel(magnetEnchant);
            level = combineLevels(oldLevel, level);

            if (level > magnetEnchant.getMaxLevel()){
                level = magnetEnchant.getMaxLevel();
            }

            meta.addEnchant(magnetEnchant, level, false);
        }

        addMagnetEnchantLore(meta, level);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack enchantBook(ItemStack item, int level, boolean forced){
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        Enchantment enchant = Enchantment.getByKey(magnetEnchant.getKey());
        if (meta == null) return item;
        if (!magnetEnchant.canEnchantItem(item)) return item;

        if (forced){
            meta.addStoredEnchant(enchant, level, true);
            item.setItemMeta(meta);
            return item;
        }

        int oldLevel = meta.getStoredEnchantLevel(magnetEnchant);

        level = combineLevels(oldLevel, level);

        if (level > magnetEnchant.getMaxLevel()){
            level = magnetEnchant.getMaxLevel();
        }

        meta.addStoredEnchant(enchant, level, false);
        addMagnetEnchantLore(meta, level);
        item.setItemMeta(meta);
        return item;
    }

    public void addMagnetEnchantLore(ItemMeta meta, int level) {
        if (meta == null || level < 1) {
            LogUtils.debug("Skipping lore update: invalid meta or level {}", level);
            return;
        }

        String romanLevel = toRoman(level);
        String loreLine = ChatColor.translateAlternateColorCodes('&', loreFormat.replace("%level%", romanLevel));

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String magnetPrefix = ChatColor.stripColor(loreFormat.split("%level%")[0]);


        lore.removeIf(line -> ChatColor.stripColor(line).contains(magnetPrefix));

        lore.add(0, loreLine);

        meta.setLore(lore);
    }

    private int combineLevels(int oldLevel, int newLevel){
        if (oldLevel == 0){
            return newLevel;
        }

        if (oldLevel == newLevel){
            return oldLevel;
        }
        return Math.max(oldLevel, newLevel);
    }

    public Integer getMagnetLevel(ItemStack item){

        Map<Enchantment, Integer> enchants;

        if (item.getType() == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            if (meta == null) return 0;

            enchants = meta.getStoredEnchants();
        }else {
            if (item.getItemMeta() == null) return 0;
            enchants = item.getItemMeta().getEnchants();
        }

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
            if (entry.getKey() == magnetEnchant) return entry.getValue();
        }
        return 0;
    }

    public String toRoman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(level);
        };
    }
}
