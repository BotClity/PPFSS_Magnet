// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command;

import com.ppfss.magnet.service.MagnetEnchantService;
import com.ppfss.magnet.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.IntStream;

public class EnchantSub extends SubCommand{
    private final MagnetEnchantService enchantService;
    private final int maxLevel;

    public EnchantSub(MagnetEnchantService enchantService) {
        super("enchant", List.of("зачаровать"));

        this.enchantService = enchantService;
        this.maxLevel = enchantService.getMagnetEnchant().getMaxLevel();
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(sender instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()){
            sender.sendMessage(ColorUtils.color(
                    "&cВозьмите необходимый предмет в руку."
            ));
            return;
        }

        int enchantLevel = 1;

        if (args.length != 0){
            try{
                enchantLevel = Integer.parseInt(args[0]);
            }catch (NumberFormatException exception){
                sender.sendMessage(ColorUtils.color("&cВы ввели не число"));
                return;
            }
        }

        item = enchantService.enchantMagnet(item, enchantLevel, true);

        sender.sendMessage("&aПредмет успешно зачарован!");
    }

    @Override
    public String getPermission(CommandSender sender, String... args) {
        return "magnet.enchant";
    }

    @Override
    public List<String> complete(CommandSender sender, String... args) {
        if (args.length == 1){
            return IntStream.rangeClosed(1, maxLevel)
                    .mapToObj(enchantService::toRoman)
                    .toList();
        }
        return null;
    }
}
