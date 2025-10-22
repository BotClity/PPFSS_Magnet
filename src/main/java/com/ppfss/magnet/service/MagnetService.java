// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.service;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.ppfss.libs.message.Placeholders;
import com.ppfss.magnet.config.Config;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.model.DefaultItemData;
import com.ppfss.magnet.model.MagnetData;
import com.ppfss.magnet.model.Reloadable;
import com.ppfss.magnet.model.TasksSettings;
import com.ppfss.magnet.task.MagnetRunnable;
import com.ppfss.magnet.task.PerformanceRunnable;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("SameParameterValue")
@Getter
public class MagnetService implements Reloadable {
    private final Map<UUID, MagnetData> activeMagnets = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private BukkitTask magnetTask;
    private BukkitTask performanceRunnable;
    private boolean permissionRequired = false;
    private DefaultItemData itemData;
    @Setter
    private boolean isWorkingMagnet = true;

    public MagnetService(Plugin plugin) {
        this.plugin = plugin;

        reload();
    }

    @Override
    public void reload() {
        if (magnetTask != null) {
            magnetTask.cancel();
        }
        if (performanceRunnable != null) {
            performanceRunnable.cancel();
        }

        TasksSettings settings = Config.getInstance().getTasksSettings();

        BukkitRunnable magnetRunnable = new MagnetRunnable(
                this,
                player -> getMagnetData(player, false) != null
        );

        BukkitRunnable performanceRunnable = new PerformanceRunnable(
                settings.getCpuLimit(),
                settings.getRamLimit(),
                this
        );

        this.magnetTask = magnetRunnable.runTaskTimer(plugin, 40, settings.getMagnetPeriod());
        this.performanceRunnable = performanceRunnable.runTaskTimer(plugin, 40, settings.getPerformancePeriod());

        this.permissionRequired = Config.getInstance().isPermissionRequired();

        this.itemData = Config.getInstance().getDefaultItemData();
        if (itemData.getType() == null) itemData.setType(Material.PLAYER_HEAD);
    }


    public ItemStack getDefaultMagnet(MagnetData data){
        ItemStack itemStack = new ItemStack(itemData.getType());
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) itemMeta = Bukkit.getItemFactory().getItemMeta(itemData.getType());


        String headValue = itemData.getHeadValue();
        if (itemData.getType() == Material.PLAYER_HEAD && !headValue.isEmpty()){
            SkullMeta skullMeta = (SkullMeta) itemMeta;

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

            profile.setProperty(new ProfileProperty("textures", headValue));

            skullMeta.setPlayerProfile(profile);
        }

        Placeholders placeholders = Placeholders
                .of("radius", String.valueOf(data.radius()))
                .add("limit",  String.valueOf(data.limit()))
                .add("strength", String.valueOf(data.strength()));

        itemMeta.displayName(itemData.getName().getComponents().get(0));

        itemMeta.lore(itemData.getLore().getComponents(placeholders));

        itemStack.setItemMeta(itemMeta);

        setupMagnetData(itemStack, data);

        return itemStack;
    }

    public void verifyPlayer(Player player) {
        if (permissionRequired){
            if (!player.hasPermission("magnet.use")){
                MessageConfig.getInstance().getNoPermission().sendActionBar(player);
                return;
            }
        }
        MagnetData data = getMagnetData(player, true);

        if (data == null) {
            unverifyPlayer(player);
            return;
        }

        registerActiveMagnet(player.getUniqueId(), data);
        MessageConfig.getInstance().getMagnetActivated().sendActionBar(player);
    }

    public void unverifyPlayer(Player player) {
        MagnetData activeMagnet = removeActiveMagnet(player.getUniqueId());

        if (activeMagnet == null) return;
        MessageConfig.getInstance().getMagnetDeactivated().sendActionBar(player);
    }

    public MagnetData getMagnetData(ItemStack item) {
        if (item == null) return null;
        return NBT.get(
                item,
                nbt -> {
                    ReadableNBT magnet = nbt.getCompound("magnet");
                    if (magnet == null) return null;
                    int radius = magnet.getInteger("radius");
                    double strength = magnet.getDouble("strength");
                    int limit = magnet.getInteger("limit");

                    return new MagnetData(
                            radius,
                            strength,
                            limit
                    );
                }
        );
    }

    public MagnetData getMagnetData(Player player, boolean best) {
        MagnetData magnetData = null;
        PlayerInventory inventory = player.getInventory();

        ItemStack[] items = {
                inventory.getItemInMainHand(),
                inventory.getItemInOffHand(),
                inventory.getHelmet(),
                inventory.getChestplate(),
                inventory.getLeggings(),
                inventory.getBoots()
        };


        for (ItemStack item : items) {
            if (item == null) continue;
            if (item.getType().isAir()) continue;

            MagnetData data = getMagnetData(item);

            if (data != null) {
                if (magnetData == null) {
                    magnetData = data;
                    continue;
                }
                if (!best) return magnetData;
                magnetData = magnetData.compare(data);
            }
        }
        return magnetData;
    }

    public void setupMagnetData(ItemStack item, MagnetData data) {
        NBT.modify(item, nbt -> {
            ReadWriteNBT magnetData = nbt.getOrCreateCompound("magnet");

            magnetData.setInteger("radius", data.radius());
            magnetData.setDouble("strength", data.strength());
            magnetData.setInteger("limit", data.limit());
        });
    }

    public void removeMagnetData(ItemStack item){
        NBT.modify(item, nbt -> {
            nbt.removeKey("magnet");
        });
    }

    public void registerActiveMagnet(UUID uuid, MagnetData magnet) {
        activeMagnets.put(uuid, magnet);
    }

    public MagnetData removeActiveMagnet(UUID uuid) {
        return activeMagnets.remove(uuid);
    }


}
