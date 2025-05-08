package com.ppfss.magnet.domain;

import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class MagnetEnchantment extends Enchantment {
    private final String name;
    @Setter
    private int maxLevel;

    public MagnetEnchantment(NamespacedKey key, String name) {
        super(key);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        if (item == null) return false;

        Material type = item.getType();
        return type == Material.BOOK
                || type == Material.ENCHANTED_BOOK
                || getItemTarget().includes(item);
    }
}
