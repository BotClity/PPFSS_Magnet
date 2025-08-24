// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command.magnet;

import com.ppfss.magnet.command.AbstractCommand;
import com.ppfss.magnet.service.MagnetService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class MagnetCommand extends AbstractCommand {


    public MagnetCommand(Plugin plugin, MagnetService magnetService) {
        super("magnet", List.of("магнит"), plugin);

        this.registerSubCommand(new SubGive(magnetService));
        this.registerSubCommand(new SubEnchant(magnetService));
        this.registerSubCommand(new SubRemove(magnetService));
    }

    @Override
    protected void handle(CommandSender sender, Command command, String commandLabel, String[] args) {
        sender.sendMessage("Доступные команды:");
        subCommands.values().forEach(subCommand -> {
            if (!sender.hasPermission(subCommand.getPermission(sender, command, commandLabel, args))) return;
            subCommand.sendUsage(sender, command, commandLabel, args);
        });
    }
}
