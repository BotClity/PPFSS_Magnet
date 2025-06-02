// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command;

import com.ppfss.magnet.service.MagnetEnchantService;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class MagnetCommand extends AbstractCommand {
    public MagnetCommand(Plugin plugin, MagnetEnchantService enchantService) {
        super("magnet", plugin);

        registerSubCommand(new EnchantSub(enchantService));


        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection section = cfg.getConfigurationSection("magnet-item");
        if (section == null) {
            Configuration defaultCfg = cfg.getDefaults();
            if (defaultCfg == null) throw new IllegalStateException("Can't get configuration for default magnet item");
            section = defaultCfg.getConfigurationSection("magnet-item");
        }
        if (section == null) throw new IllegalStateException("Can't get configuration for magnet item");

        registerSubCommand(new GiveSub(enchantService, section));
    }

}
