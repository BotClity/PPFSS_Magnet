package com.ppfss.magnet;

import com.ppfss.magnet.cache.MagnetCache;
import com.ppfss.magnet.command.MagnetCommand;
import com.ppfss.magnet.domain.MagnetData;
import com.ppfss.magnet.listener.EnchantmentListener;
import com.ppfss.magnet.listener.MagnetListener;
import com.ppfss.magnet.service.MagnetEnchantService;
import com.ppfss.magnet.service.TaskService;
import com.ppfss.magnet.task.MagnetTask;
import com.ppfss.magnet.task.PerformanceTask;
import com.ppfss.magnet.utils.ColorCode;
import com.ppfss.magnet.utils.LogUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class PPFSS_Magnet extends JavaPlugin {
    private Metrics metrics;
    private Logger logger;
    private MagnetCache cache;
    private MagnetEnchantService enchantService;
    private TaskService taskService;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.logger = getLogger();

        FileConfiguration config = getConfig();

        registerMetrics();


        this.cache = new MagnetCache();
        loadCache();

        this.enchantService = new MagnetEnchantService(this);

        LogUtils.log(Level.INFO, "PPFSS_MAGNET", ColorCode.GREEN);

        LogUtils.log(Level.INFO, "Регистрация слушателей", ColorCode.GREEN);
        registerListeners();

        LogUtils.log(Level.INFO, "Регистрация команд", ColorCode.GREEN);
        registerCommands();

        Particle particle = null;

        if (config.getBoolean("enchant.particles.enabled", false)) {
            particle = Particle.valueOf(config.getString("enchant.particles.type"));
        }

        MagnetTask magnetTask = new MagnetTask(cache, 0, config.getInt("enchant.period-ticks", 10), particle);

        long period = config.getLong("performance.check-interval-ticks", 1200);
        double cpu = config.getDouble("performance.cpu-threshold", 80.0d);
        double ram = config.getDouble("performance.ram-threshold", 80.0d);

        PerformanceTask performanceTask = new PerformanceTask(magnetTask, period, cpu, ram);


        this.taskService = new TaskService(magnetTask, performanceTask, this);


        taskService.startTasks();
    }

    private void loadCache() {
        FileConfiguration cfg = getConfig();


        ConfigurationSection levelsSection = cfg.getConfigurationSection("enchant.levels");
        if (levelsSection != null) {
            for (String key : levelsSection.getKeys(false)) {
                try {
                    int level = Integer.parseInt(key);

                    ConfigurationSection section = levelsSection.getConfigurationSection(key);
                    double radius = section.getDouble("radius", 5);
                    double speed = section.getDouble("speed", 0.1);
                    int limit = section.getInt("limit", 10);

                    MagnetData data = new MagnetData(radius, speed, limit);

                    cache.addMagnetLevel(level, data);
                } catch (NumberFormatException exception) {
                    logger.warning("Can't recognise key: " + key);
                }
            }
        }else{
            throw new IllegalStateException("Can't find levels section");
        }
    }

    private void registerCommands() {
        new MagnetCommand(this, enchantService);
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        FileConfiguration config = getConfig();
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages == null) {
            Configuration defaultConfig = config.getDefaults();
            if (defaultConfig == null) throw new IllegalStateException("No default configuration found");
            messages = defaultConfig.getConfigurationSection("messages");
            if (messages == null) throw new IllegalStateException("Can't get messages from config");
        }

        pm.registerEvents(new MagnetListener(this, cache, enchantService, messages), this);
        pm.registerEvents(new EnchantmentListener(this, enchantService), this);
    }

    private void registerMetrics() {
        metrics = new Metrics(this, 23867);
    }
}
