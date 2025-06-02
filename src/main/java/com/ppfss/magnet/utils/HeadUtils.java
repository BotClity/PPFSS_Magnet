// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.utils;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HeadUtils {
    private HeadUtils(){}

    public static ItemStack applyValue(ItemStack item, String value){
        if (item.getType() != Material.PLAYER_HEAD) return item;

        NBT.modify(item, nbt ->{
            ReadWriteNBT skullOwner = nbt.getOrCreateCompound("SkullOwner");

            skullOwner.setUUID("Id", UUID.randomUUID());

            skullOwner.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", value);
        });

        return item;
    }
}
