// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command.magnet;

import com.ppfss.libs.command.SubCommand;
import com.ppfss.libs.config.YamlConfigLoader;
import com.ppfss.magnet.config.Config;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.service.MagnetService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SubReload extends SubCommand {
    private final MagnetService magnetService;
    private final YamlConfigLoader configLoader;

    public SubReload(MagnetService magnetService, YamlConfigLoader configLoader) {
        super("reload");

        this.magnetService = magnetService;
        this.configLoader = configLoader;
    }


    private String getPermission(){
        return "magnet.reload";
    }

    @Override
    public String getPermission(CommandSender sender, Command command, String label, String... args) {
        return getPermission();
    }

    @Override
    public void noPermission(CommandSender sender, Command command, String label, String... args) {
        MessageConfig.getInstance().getNoPermission().send(sender);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        Config.load(configLoader);
        MessageConfig.load(configLoader);

        magnetService.reload();

        MessageConfig.getInstance().getPluginReloaded().send(sender);
    }

    @Override
    public void sendUsage(CommandSender sender, Command command, String label, String... args) {
        if (!sender.hasPermission(getPermission())) {
            noPermission(sender, command, label, args);
            return;
        }

        MessageConfig.getInstance().getPluginReloadUsage().send(sender);
    }
}
