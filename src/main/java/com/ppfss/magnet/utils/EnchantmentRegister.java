// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.utils;

import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;

public class EnchantmentRegister {
    private EnchantmentRegister() {}
    public static void registerEnchantments(Enchantment enchantment) {
        try{
            Field accepting = Enchantment.class.getDeclaredField("acceptingNew");
            accepting.setAccessible(true);
            accepting.set(null, true);
            Enchantment.registerEnchantment(enchantment);
            accepting.set(null, false);
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
