// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command.magnet;

import com.ppfss.libs.command.SubCommand;
import com.ppfss.libs.message.Message;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.model.MagnetData;
import com.ppfss.magnet.service.MagnetService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SubEnchant extends SubCommand {
    private final Message USAGE = new Message(
            "<yellow>/magnet enchant {Сила} {Радиус} {Лимит} <white>- зачарование предмета в руке"
    );
    private final MagnetService magnetService;

    public SubEnchant( MagnetService magnetService) {
        super("enchant");

        this.magnetService = magnetService;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        MessageConfig messageConfig = MessageConfig.getInstance();

        if (!(sender instanceof Player player)){
            sender.sendMessage("Only players can use this command!");
            return;
        }

        if (args.length < 3){
            USAGE.send(sender);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()){
            messageConfig.getNoItemInHand().send(sender);
            return;
        }

        MagnetData magnetData;
        try{
            double strength = Double.parseDouble(args[1]);
            int radius = Integer.parseInt(args[2]);
            int limit = Integer.parseInt(args[3]);

            magnetData = new MagnetData(radius, strength, limit);
        }catch (NumberFormatException e){
            messageConfig.getNotNumber().send(sender);
            return;
        }

        magnetService.setupMagnetData(item, magnetData);
        messageConfig.getMagnetEnchanted().send(sender);
    }

    @Override
    public String getPermission(CommandSender sender, Command command, String label, String... args) {
        return getPermission();
    }

    @Override
    public List<String> complete(CommandSender sender, String... args) {
        if (!(sender.hasPermission(getPermission()))){
            return null;
        }

        return switch (args.length){
            case 1 -> List.of("Сила");
            case 2 -> List.of("Радиус");
            case 3 -> List.of("Лимит");
            default -> null;
        };
    }

    private String getPermission(){
        return "magnet.enchant";
    }

    @Override
    public void sendUsage(CommandSender sender, Command command, String label, String... args) {
        USAGE.send(sender);
    }
}
