// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.utils;

import org.bukkit.ChatColor;

public class ColorUtils {
    private ColorUtils(){}

    public static String color(String string){
        return string.replace('&', '§');
    }

    public static String remove(String string){
        return ChatColor.stripColor(color(string));
    }
}
