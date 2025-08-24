// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command.magnet;

import com.ppfss.magnet.command.SubCommand;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.message.Message;
import com.ppfss.magnet.service.MagnetService;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SubRemove extends SubCommand {
    private final Message USAGE = new Message(
            "<yellow>/magnet remove <white>- удаляет механику магнита с предмета в руке"
    );
    private final Message SUCCESS = new Message("<green>Эффект магнита успешно снят");
    private final String PERMISSION = "magnet.remove";
    private final MagnetService magnetService;

    public SubRemove(MagnetService magnetService) {
        super("remove");
        this.magnetService = magnetService;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(sender instanceof Player player)){
            sender.sendMessage("Only for players");
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();

        if (item.getType() == Material.AIR) {
            MessageConfig.getInstance().getNoItemInHand().send(sender);
            return;
        }

        magnetService.removeMagnetData(item);
        SUCCESS.send(sender);
    }

    @Override
    public String getPermission(CommandSender sender, Command command, String label, String... args) {
        return PERMISSION;
    }

    @Override
    public void noPermission(CommandSender sender, Command command, String label, String... args) {
        MessageConfig.getInstance().getNoPermission().send(sender);
    }

    @Override
    public void sendUsage(CommandSender sender, Command command, String label, String... args) {
        USAGE.send(sender);
    }
}
