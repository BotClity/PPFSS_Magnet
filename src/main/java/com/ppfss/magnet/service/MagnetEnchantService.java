package com.ppfss.magnet.service;

import com.ppfss.magnet.domain.MagnetEnchantment;
import com.ppfss.magnet.utils.ColorUtils;
import com.ppfss.magnet.utils.EnchantmentRegister;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

@Getter
public class MagnetEnchantService {
    private static final String NBT_KEY = "magnet_enchant";
    private final NamespacedKey magnetKey;
    private final Enchantment magnetEnchant;
    private final String displayName;


    public MagnetEnchantService(Plugin plugin) {
        magnetKey = new NamespacedKey(plugin, "magnet");
        FileConfiguration cfg = plugin.getConfig();


        String name = cfg.getString("enchant.name", "Магнит");
        displayName = ColorUtils.color(
                cfg.getString("enchant.displayName", "§bМагнит")
        );

        magnetEnchant = new MagnetEnchantment(magnetKey, ColorUtils.color(name));

        EnchantmentRegister.registerEnchantments(magnetEnchant);
    }

    public ItemStack removeEnchantment(ItemStack item) {
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

    public boolean hasEnchant(ItemStack item){
        if (item.getType() == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            return meta != null && meta.hasStoredEnchant(magnetEnchant);
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasEnchant(magnetEnchant);
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
        item.setItemMeta(meta);
        return item;
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



//    public void applyEnchant(ItemStack item, int level) {
//        if (item == null) return;
//
//        NBT.modify(item, nbt -> {nbt.setInteger(NBT_KEY, level);});
//        updateLore(item, level);
//    }
//
//    public void removeEnchant(ItemStack item) {
//        if (item == null) return;
//
//        NBT.modify(item, nbt -> {nbt.removeKey(NBT_KEY);});
//        updateLore(item, 0);
//    }
//
//    public boolean hasEnchant(ItemStack item) {
//        if (item == null) return false;
//
//        return NBT.get(item, nbt -> (boolean) nbt.hasTag(NBT_KEY));
//    }
//
//    public int getEnchantLevel(ItemStack item) {
//        if (item == null) return -1;
//
//        return NBT.get(item, nbt -> (int) nbt.getInteger(NBT_KEY));
//    }
//
//    private void updateLore(ItemStack item, int level) {
//        if (item == null) return;
//
//        ItemMeta meta = item.getItemMeta();
//        if (meta == null) return;
//
//        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
//        lore.removeIf(line -> line.contains(ColorUtils.remove(displayName)));
//
//        if (level > 0) {
//            lore.add(displayName + " " + toRoman(level));
//        }
//
//        meta.setLore(lore);
//        item.setItemMeta(meta);
//    }

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
