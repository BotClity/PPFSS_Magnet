// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command;

import com.ppfss.magnet.service.MagnetEnchantService;
import com.ppfss.magnet.utils.ColorUtils;
import com.ppfss.magnet.utils.HeadUtils;
import com.ppfss.magnet.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GiveSub extends SubCommand {
    private final MagnetEnchantService enchantService;
    private final int maxLevel;
    private final int defaultLevel;
    private final ItemStack magnetItem;

    public GiveSub(MagnetEnchantService enchantService, ConfigurationSection section) {
        super("give");
        this.defaultLevel = section.getInt("defaultLevel", 1);

        this.enchantService = enchantService;
        this.maxLevel = enchantService.getMagnetEnchant().getMaxLevel();
        this.magnetItem = initItem(section);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (!(sender instanceof Player player)) return;

        int level = defaultLevel;
        Player receiver = player;

        if (args.length > 0) {
            try{
                level = Integer.parseInt(args[0]);
            }catch (NumberFormatException exception){
                sender.sendMessage("§a[PPFSS_Magnet]§cНекорректный уровень зачарования.");
                return;
            }

            if (args.length > 1) {
                receiver = Bukkit.getPlayer(args[1]);
                if (receiver == null) {
                    sender.sendMessage("§a[PPFSS_Magnet]§cИгрок с ником {} не найден.");
                    return;
                }
            }
        }

        ItemStack item = magnetItem.clone();

        PlayerInventory inventory = receiver.getInventory();

        int freeSlot = inventory.firstEmpty();
        if (freeSlot == -1) {
            sender.sendMessage("§a[PPFSS_Magnet]§cИнвентарь переполнен.");
            return;
        }

        enchantService.enchantMagnet(item, level, true);

        inventory.setItem(freeSlot, item);

        sender.sendMessage("§a[PPFSS_Magnet]§aМагнит успешно выдан!");
    }



    @Override
    public List<String> complete(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) return null;
        switch (args.length){
            case 1 ->{
                List<String> list = new ArrayList<>();
                list.add("Уровень");
                list.addAll(Utils.getNumberRange(maxLevel));

                return list;
            }
            case 2 -> {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }

            default -> {
                return null;
            }
        }
    }

    @Override
    public String getPermission(CommandSender sender, String... args) {
        return "magnet.give";
    }

    private ItemStack initItem(ConfigurationSection section) {
        Material material = Material.getMaterial(section.getString("type"));
        String name = ColorUtils.color(section.getString("name", "&cМаг&9нит"));


        if (material == null) material = Material.PLAYER_HEAD;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        String value = section.getString("value", null);

        if (value != null) {
            HeadUtils.applyValue(item, value);
        }

        return item;
    }
}
