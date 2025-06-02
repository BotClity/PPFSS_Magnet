package com.ppfss.magnet.domain;

import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MagnetEnchantment extends Enchantment {
    private final String name;
    private final Set<String> allowedItems;
    @Setter
    private int maxLevel;

    public MagnetEnchantment(NamespacedKey key, String name, int maxLevel, List<String> allowedItems) {
        super(key);
        this.name = name;
        this.maxLevel = maxLevel;
        this.allowedItems = allowedItems != null
                ? allowedItems.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toCollection(HashSet::new))
                : new HashSet<>();
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


        String name = item.getType().name();
        for (String allowedItem : allowedItems) {
            if (name.endsWith(allowedItem)) {
                return true;
            }
        }
        return false;
    }
}
