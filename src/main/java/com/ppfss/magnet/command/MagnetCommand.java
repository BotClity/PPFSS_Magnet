// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command;

import com.ppfss.magnet.service.MagnetEnchantService;
import org.bukkit.plugin.Plugin;

public class MagnetCommand extends AbstractCommand {
    public MagnetCommand(Plugin plugin, MagnetEnchantService enchantService) {
        super("magnet", plugin);

        registerSubCommand(new EnchantSub(enchantService));
    }

}
