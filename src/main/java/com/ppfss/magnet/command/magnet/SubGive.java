// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command.magnet;

import com.ppfss.libs.command.SubCommand;
import com.ppfss.libs.message.Message;
import com.ppfss.libs.message.Placeholders;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.model.MagnetData;
import com.ppfss.magnet.service.MagnetService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class SubGive extends SubCommand {
    private final Message USAGE = new Message(
            "<yellow>/magnet give {Игрок} {Сила} {Радиус} {Лимит} <white>- выдача магнита игроку.",
            "<yellow>{Лимит} <white>- максимальное кол-во притягивающихся блоков"
    );
    private final MagnetService magnetService;

    public SubGive(MagnetService magnetService) {
        super("give");
        this.magnetService = magnetService;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        MessageConfig messageConfig = MessageConfig.getInstance();
        if (args.length < 4) {
            sendUsage(sender, command, label, args);
            return;
        }

        String name = args[0];
        Player player = Bukkit.getPlayer(name);

        if (player == null) {
            messageConfig.getPlayerNotFound().send(sender, Placeholders.of("player", name));
            return;
        }

        MagnetData magnetData;
        try{
            double strength = Double.parseDouble(args[1]);
            int radius = Integer.parseInt(args[2]);
            int limit = Integer.parseInt(args[3]);

            magnetData = new MagnetData(radius, strength, limit);
        }catch (NumberFormatException exception){
            messageConfig.getNotNumber().send(sender);
            return;
        }

        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            messageConfig.getNotEnoughSpace().send(sender);
            return;
        }

        inventory.addItem(magnetService.getDefaultMagnet(magnetData));
        messageConfig.getMagnetGiven().send(sender);
    }

    @Override
    public List<String> complete(CommandSender sender, String... args) {
        if (!sender.hasPermission(getPermission()) && !sender.isOp()) {
            return null;
        }

        return switch (args.length) {
            case 1 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            case 2 -> List.of("Сила");
            case 3 -> List.of("Радиус");
            case 4 -> List.of("Лимит");
            default -> null;
        };
    }

    @Override
    public void noPermission(CommandSender sender, Command command, String label, String... args) {
        MessageConfig.getInstance().getNoPermission().send(sender);
    }

    @Override
    public String getPermission(CommandSender sender, Command command, String label, String... args) {
        return getName();
    }

    @Override
    public void sendUsage(CommandSender sender, Command command, String label, String... args) {
        USAGE.send(sender);
    }

    private String getPermission() {
        return "magnet.give";
    }
}
